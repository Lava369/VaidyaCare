<?php
require_once "config.php";

$conn = getDBConnectionSafe();
if (!$conn) {
    sendResponse(false, "Database unavailable", null, 500);
}

/* ---------- READ JSON ---------- */
$data = json_decode(file_get_contents("php://input"), true);

$email    = trim($data["email"] ?? "");
$mobile   = trim($data["mobile"] ?? "");
$password = $data["password"] ?? "";

/* ---------- BASIC VALIDATION ---------- */
if ($password === "") {
    sendResponse(false, "Password required", null, 400);
}

/* ---------- NORMALIZE MOBILE ---------- */
/* Handle both formats: +91XXXXXXXXXX and XXXXXXXXXX */
if ($mobile !== "") {
    // Strip +91 prefix if present
    $mobile = preg_replace('/^\+91/', '', $mobile);
    // Also try with +91 prefix for database lookup (signup stores with +91)
    $mobileWithPrefix = "+91" . $mobile;
}

/* ---------- BUILD QUERY ---------- */
if ($email !== "") {
    $stmt = $conn->prepare(
        "SELECT id, full_name, email, mobile, password
         FROM users WHERE email = ?"
    );
    $stmt->bind_param("s", $email);
} elseif ($mobile !== "") {
    // Try both formats: with and without +91 prefix
    $stmt = $conn->prepare(
        "SELECT id, full_name, email, mobile, password
         FROM users WHERE mobile = ? OR mobile = ?"
    );
    $stmt->bind_param("ss", $mobile, $mobileWithPrefix);
} else {
    sendResponse(false, "Email or mobile required", null, 400);
}

$stmt->execute();
$result = $stmt->get_result();
$user = $result->fetch_assoc();
$stmt->close();

/* ---------- VERIFY USER ---------- */
if (!$user || !password_verify($password, $user["password"])) {
    sendResponse(false, "Invalid credentials", null, 401);
}

/* ---------- SUCCESS ---------- */
sendResponse(true, "Login successful", [
    "user_id"   => $user["id"],
    "full_name" => $user["full_name"],
    "email"     => $user["email"],
    "mobile"    => $user["mobile"]
]);
