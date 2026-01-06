<?php
ob_start();
header("Content-Type: application/json; charset=UTF-8");
require_once 'config.php';

/* ---------- ONLY POST ---------- */
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    ob_clean();
    echo json_encode([
        "success" => false,
        "message" => "Method not allowed. Use POST."
    ]);
    exit;
}

/* ---------- READ INPUT (JSON OR FORM) ---------- */
$raw  = file_get_contents("php://input");
$data = json_decode($raw, true);

/* Fallback for application/x-www-form-urlencoded */
if (!is_array($data)) {
    $data = $_POST;
}

$password = trim($data["password"] ?? "");
$email    = trim($data["email"] ?? "");
$mobile   = trim($data["mobile"] ?? "");

/* ---------- VALIDATION ---------- */
if ($password === "") {
    ob_clean();
    echo json_encode([
        "success" => false,
        "message" => "Password is required"
    ]);
    exit;
}

if ($email === "" && $mobile === "") {
    ob_clean();
    echo json_encode([
        "success" => false,
        "message" => "Email or mobile number is required"
    ]);
    exit;
}

/* ---------- DB CONNECTION ---------- */
$conn = getDBConnection();

/* ---------- FETCH DOCTOR ---------- */
if ($email !== "") {
    $stmt = $conn->prepare("
        SELECT
            doctor_id,
            full_name,
            email,
            mobile,
            license_no,
            password,
            verification_status
        FROM doctors
        WHERE email = ?
        LIMIT 1
    ");
    $stmt->bind_param("s", $email);
} else {
    $stmt = $conn->prepare("
        SELECT
            doctor_id,
            full_name,
            email,
            mobile,
            license_no,
            password,
            verification_status
        FROM doctors
        WHERE mobile = ?
        LIMIT 1
    ");
    $stmt->bind_param("s", $mobile);
}

$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    $stmt->close();
    $conn->close();
    ob_clean();
    echo json_encode([
        "success" => false,
        "message" => "Invalid credentials"
    ]);
    exit;
}

$doctor = $result->fetch_assoc();

/* ---------- CHECK APPROVAL ---------- */
if ($doctor["verification_status"] !== "approved") {
    $stmt->close();
    $conn->close();
    ob_clean();
    echo json_encode([
        "success" => false,
        "message" => "Your account is pending admin approval"
    ]);
    exit;
}

/* ---------- VERIFY PASSWORD ---------- */
if (!password_verify($password, $doctor["password"])) {
    $stmt->close();
    $conn->close();
    ob_clean();
    echo json_encode([
        "success" => false,
        "message" => "Invalid credentials"
    ]);
    exit;
}

/* ---------- SUCCESS ---------- */
$stmt->close();
$conn->close();

ob_clean();
echo json_encode([
    "success" => true,
    "message" => "Login successful",
    "doctor" => [
        "doctor_id" => (int)$doctor["doctor_id"],
        "full_name" => $doctor["full_name"],
        "email" => $doctor["email"],
        "mobile" => $doctor["mobile"],
        "license_no" => $doctor["license_no"]
    ]
]);
exit;
