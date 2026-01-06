<?php
require_once "config.php";

$conn = getDBConnectionSafe();
if (!$conn) {
    sendResponse(false, "Database unavailable", null, 500);
}

/* ---------- READ JSON ---------- */
$data = json_decode(file_get_contents("php://input"), true);

$full_name = trim($data["full_name"] ?? "");
$email     = trim($data["email"] ?? "");
$mobile    = normalizeMobile($data["mobile"] ?? "");
$age       = (int)($data["age"] ?? 0);
$gender    = trim($data["gender"] ?? "");
$password  = $data["password"] ?? "";

/* ---------- VALIDATION ---------- */
if (
    $full_name === "" ||
    !isValidEmail($email) ||
    !isValidMobile($mobile) ||
    $age <= 0 ||
    !in_array($gender, ["Male","Female","Other"]) ||
    strlen($password) < 6
) {
    sendResponse(false, "Invalid input data", null, 400);
}

/* ---------- DUPLICATE CHECK ---------- */
$stmt = $conn->prepare("SELECT id FROM users WHERE email = ? OR mobile = ?");
$stmt->bind_param("ss", $email, $mobile);
$stmt->execute();
$stmt->store_result();

if ($stmt->num_rows > 0) {
    sendResponse(false, "Email or mobile already registered", null, 409);
}
$stmt->close();

/* ---------- INSERT USER ---------- */
$hashed = password_hash($password, PASSWORD_DEFAULT);
$patientId = generatePatientId();

$stmt = $conn->prepare(
    "INSERT INTO users (full_name, email, mobile, age, gender, password)
     VALUES (?, ?, ?, ?, ?, ?)"
);
$stmt->bind_param(
    "sssiss",
    $full_name,
    $email,
    $mobile,
    $age,
    $gender,
    $hashed
);

if (!$stmt->execute()) {
    sendResponse(false, "Signup failed", null, 500);
}

$userId = $stmt->insert_id;

sendResponse(true, "Account created successfully", [
    "user_id"   => $userId,
    "full_name" => $full_name,
    "email"     => $email,
    "mobile"    => $mobile
]);
