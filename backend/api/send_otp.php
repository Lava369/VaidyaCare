<?php
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

header("Content-Type: application/json; charset=UTF-8");
date_default_timezone_set("Asia/Kolkata");

require_once __DIR__ . "/config.php";
require_once __DIR__ . "/.env.php";

require_once __DIR__ . "/PHPMailer-7.0.1/src/Exception.php";
require_once __DIR__ . "/PHPMailer-7.0.1/src/PHPMailer.php";
require_once __DIR__ . "/PHPMailer-7.0.1/src/SMTP.php";

function respond($status, $message, $code = 200) {
    http_response_code($code);
    echo json_encode(["status"=>$status,"message"=>$message]);
    exit;
}

$email = trim($_POST['email'] ?? '');
if ($email === '') respond("error", "Email is required", 400);

/* ✅ SAFE USER CHECK (NO get_result) */
$stmt = $conn->prepare("SELECT id, full_name FROM users WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$stmt->bind_result($user_id, $name);

if (!$stmt->fetch()) {
    respond("error", "Email not found", 404);
}
$stmt->close();

/* DELETE OLD OTP */
$del = $conn->prepare("DELETE FROM password_resets WHERE email = ?");
$del->bind_param("s", $email);
$del->execute();

/* CREATE OTP */
$otp = random_int(100000, 999999);
$expires_at = date("Y-m-d H:i:s", strtotime("+10 minutes"));

$ins = $conn->prepare(
    "INSERT INTO password_resets (user_id, email, otp, expires_at)
     VALUES (?, ?, ?, ?)"
);
$ins->bind_param("isis", $user_id, $email, $otp, $expires_at);
if (!$ins->execute()) respond("error", "OTP insert failed", 500);

/* SEND EMAIL */
$mail = new PHPMailer(true);
try {
    $mail->isSMTP();
    $mail->Host = "smtp.gmail.com";
    $mail->SMTPAuth = true;
    $mail->Username = SMTP_EMAIL;
    $mail->Password = SMTP_APP_PASSWORD;
    $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
    $mail->Port = 587;

    $mail->setFrom(SMTP_EMAIL, "WorkVizo");
    $mail->addAddress($email, $name);
    $mail->isHTML(true);
    $mail->Subject = "WorkVizo - Password Reset OTP";
    $mail->Body = "<h2>Your OTP: $otp</h2><p>Valid for 10 minutes</p>";

    $mail->send();
    respond("success", "OTP sent successfully");

} catch (Exception $e) {
    respond("error", $mail->ErrorInfo, 500);
}
