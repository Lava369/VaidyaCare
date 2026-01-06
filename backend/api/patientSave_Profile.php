<?php
/**
 * Save Patient Profile API
 * POST: patientSave_profile.php
 * Body: JSON
 * Response: { "success": true/false, "message": "..." }
 */

ob_start();
error_reporting(0);
ini_set('display_errors', 0);

header("Content-Type: application/json; charset=UTF-8");

require_once "config.php";

/* ---------- RESPONSE HELPER ---------- */
function sendResponse($success, $message, $code = 200) {
    http_response_code($code);
    ob_clean();
    echo json_encode([
        "success" => $success,
        "message" => $message
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

/* ---------- ONLY POST ---------- */
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    sendResponse(false, "Only POST method allowed", 405);
}

/* ---------- READ JSON ---------- */
$raw = file_get_contents("php://input");
$data = json_decode($raw, true);

if (!is_array($data)) {
    sendResponse(false, "Invalid JSON input", 400);
}

/* ---------- REQUIRED FIELDS ---------- */
$email     = trim($data['email'] ?? '');
$full_name = trim($data['full_name'] ?? '');
$mobile    = trim($data['mobile'] ?? '');

if ($email === '' || $full_name === '' || $mobile === '') {
    sendResponse(false, "Required fields missing", 400);
}

/* ---------- OPTIONAL FIELDS (Convert empty strings to null) ---------- */
$dob         = (!empty($data['dob'])) ? trim($data['dob']) : null;
$gender      = (!empty($data['gender'])) ? trim($data['gender']) : null;
$blood_group = (!empty($data['blood_group'])) ? trim($data['blood_group']) : null;
$height_cm   = (!empty($data['height_cm'])) ? trim($data['height_cm']) : null;
$weight_kg   = (!empty($data['weight_kg'])) ? trim($data['weight_kg']) : null;
$address     = (!empty($data['address'])) ? trim($data['address']) : null;
$city        = (!empty($data['city'])) ? trim($data['city']) : null;
$state       = (!empty($data['state'])) ? trim($data['state']) : null;
$pin         = (!empty($data['pin'])) ? trim($data['pin']) : null;

/* ---------- DOB VALIDATION (NO FUTURE DATE) ---------- */
if (!empty($dob)) {
    $dobTime = strtotime($dob);
    if ($dobTime === false || $dobTime > time()) {
        sendResponse(false, "Invalid Date of Birth", 400);
    }
}

/* ---------- DB CONNECTION ---------- */
$conn = getDBConnectionSafe();
if (!$conn) {
    sendResponse(false, "Database connection failed", 500);
}

/* ---------- CHECK IF USER EXISTS ---------- */
$checkStmt = $conn->prepare("SELECT id FROM users WHERE email = ?");
if (!$checkStmt) {
    $conn->close();
    sendResponse(false, "SQL check failed: " . $conn->error, 500);
}
$checkStmt->bind_param("s", $email);
$checkStmt->execute();
$checkResult = $checkStmt->get_result();
if ($checkResult->num_rows === 0) {
    $checkStmt->close();
    $conn->close();
    sendResponse(false, "User not found", 404);
}
$checkStmt->close();

/* ---------- UPDATE PROFILE ---------- */
$stmt = $conn->prepare("
    UPDATE users SET
        full_name   = ?,
        mobile      = ?,
        dob         = ?,
        gender      = ?,
        blood_group = ?,
        height_cm   = ?,
        weight_kg   = ?,
        address     = ?,
        city        = ?,
        state       = ?,
        pin         = ?,
        updated_at  = NOW()
    WHERE email = ?
");

if (!$stmt) {
    $error = $conn->error ?: "Unknown SQL error";
    $conn->close();
    sendResponse(false, "SQL prepare failed: " . $error, 500);
}

/*
  Bind types:
  s = string (all fields are VARCHAR in database)
*/
$stmt->bind_param(
    "ssssssssssss",
    $full_name,     // s
    $mobile,        // s
    $dob,           // s
    $gender,        // s
    $blood_group,   // s
    $height_cm,      // s (VARCHAR in DB)
    $weight_kg,      // s (VARCHAR in DB)
    $address,       // s
    $city,          // s
    $state,         // s
    $pin,           // s (VARCHAR in DB)
    $email          // s
);

/* ---------- EXECUTE ---------- */
if ($stmt->execute()) {
    $stmt->close();
    $conn->close();
    sendResponse(true, "Profile updated successfully");
} else {
    $errorMsg = $stmt->error ?: "Unknown database error";
    $stmt->close();
    $conn->close();
    sendResponse(false, "Database error: " . $errorMsg, 500);
}
