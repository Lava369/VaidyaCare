<?php
header("Content-Type: application/json; charset=UTF-8");
date_default_timezone_set("Asia/Kolkata");

require_once __DIR__ . "/config.php";

/* ---------- RESPONSE HELPER ---------- */
function respond($status, $message, $code = 200) {
    http_response_code($code);
    echo json_encode([
        "status" => $status,
        "message" => $message
    ]);
    exit;
}

/* ---------- INPUT ---------- */
$email = trim($_POST['email'] ?? '');
$otp   = trim($_POST['otp'] ?? '');

if ($email === '' || $otp === '') {
    respond("error", "Email and OTP are required", 400);
}

if (!ctype_digit($otp) || strlen($otp) !== 6) {
    respond("error", "Invalid OTP format", 400);
}

/* ---------- VERIFY LATEST OTP ---------- */
$stmt = $conn->prepare(
    "SELECT id
     FROM password_resets
     WHERE email = ?
       AND otp = ?
       AND expires_at >= NOW()
     ORDER BY id DESC
     LIMIT 1"
);

$stmt->bind_param("ss", $email, $otp);
$stmt->execute();
$result = $stmt->get_result();
$row = $result->fetch_assoc();

if (!$row) {
    respond("error", "Invalid or expired OTP", 401);
}

/* ---------- OPTIONAL: DELETE OTP AFTER VERIFY ---------- */
$del = $conn->prepare("DELETE FROM password_resets WHERE id = ?");
$del->bind_param("i", $row['id']);
$del->execute();

/* ---------- SUCCESS ---------- */
respond("success", "OTP verified successfully");
