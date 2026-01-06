<?php
/**
 * Admin Login API
 * POST /api/admin_login.php
 * Body: { "email": "admin@example.com", "password": "password123" }
 */

header("Content-Type: application/json");
require_once "config.php";

// ---------- ALLOW ONLY POST ----------
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    sendResponse(false, "Only POST method is allowed", null, 405);
}

// ---------- READ JSON ----------
$input = json_decode(file_get_contents("php://input"), true);
if (!$input) {
    sendResponse(false, "Invalid JSON input", null, 400);
}

// ---------- VALIDATE INPUT ----------
$email    = strtolower(trim($input['email'] ?? ''));
$password = $input['password'] ?? '';

if ($email === '' || $password === '') {
    sendResponse(false, "Email and password are required", null, 400);
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    sendResponse(false, "Invalid email format", null, 400);
}

// ---------- DB ----------
$conn = getDBConnection();

// ---------- FETCH ADMIN ----------
$stmt = $conn->prepare(
    "SELECT admin_id, email, password, name
     FROM admins
     WHERE email = ? AND status = 'active'
     LIMIT 1"
);

$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    $stmt->close();
    $conn->close();
    sendResponse(false, "Invalid email or password", null, 401);
}

$admin = $result->fetch_assoc();
$stmt->close();

// ---------- VERIFY PASSWORD ----------
if (!password_verify($password, $admin['password'])) {
    $conn->close();
    sendResponse(false, "Invalid email or password", null, 401);
}

// ---------- SUCCESS ----------
$responseData = [
    "admin_id" => (int)$admin['admin_id'],
    "email"    => $admin['email'],
    "name"     => $admin['name']
];

$conn->close();
sendResponse(true, "Login successful", $responseData, 200);
