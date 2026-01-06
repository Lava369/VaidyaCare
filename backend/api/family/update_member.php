<?php
/**
 * Update Family Member API
 * POST: update_member.php
 * Body: JSON { "id": 1, "name": "...", "relation": "...", ... }
 * Response: { "success": true/false, "message": "..." }
 */

ob_start();
error_reporting(0);
ini_set('display_errors', 0);

header("Content-Type: application/json; charset=UTF-8");

require_once "../config.php";

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
$id = isset($data['id']) ? intval($data['id']) : 0;
$name = isset($data['name']) ? trim($data['name']) : '';
$relation = isset($data['relation']) ? trim($data['relation']) : '';
$age = isset($data['age']) ? intval($data['age']) : 0;

if ($id <= 0 || $name === '' || $relation === '' || $age <= 0) {
    sendResponse(false, "Required fields missing or invalid", 400);
}

/* ---------- OPTIONAL FIELDS ---------- */
$gender = (!empty($data['gender'])) ? trim($data['gender']) : null;
$phone = (!empty($data['phone'])) ? trim($data['phone']) : null;
$blood_group = (!empty($data['blood_group'])) ? trim($data['blood_group']) : null;
$address = (!empty($data['address'])) ? trim($data['address']) : null;
$medical_history = (!empty($data['medical_history'])) ? trim($data['medical_history']) : null;
$allergies = (!empty($data['allergies'])) ? trim($data['allergies']) : null;
$medications = (!empty($data['medications'])) ? trim($data['medications']) : null;

/* ---------- DB CONNECTION ---------- */
$conn = getDBConnectionSafe();
if (!$conn) {
    sendResponse(false, "Database connection failed", 500);
}

/* ---------- CHECK IF MEMBER EXISTS ---------- */
$checkStmt = $conn->prepare("SELECT id FROM family_members WHERE id = ?");
if (!$checkStmt) {
    $conn->close();
    sendResponse(false, "SQL check failed: " . $conn->error, 500);
}
$checkStmt->bind_param("i", $id);
$checkStmt->execute();
$checkResult = $checkStmt->get_result();
if ($checkResult->num_rows === 0) {
    $checkStmt->close();
    $conn->close();
    sendResponse(false, "Family member not found", 404);
}
$checkStmt->close();

/* ---------- UPDATE FAMILY MEMBER ---------- */
$stmt = $conn->prepare("
    UPDATE family_members SET
        name = ?,
        relation = ?,
        age = ?,
        gender = ?,
        phone = ?,
        blood_group = ?,
        address = ?,
        medical_history = ?,
        allergies = ?,
        medications = ?,
        updated_at = NOW()
    WHERE id = ?
");

if (!$stmt) {
    $error = $conn->error ?: "Unknown SQL error";
    $conn->close();
    sendResponse(false, "SQL prepare failed: " . $error, 500);
}

$stmt->bind_param(
    "ssisssssssi",
    $name,             // s
    $relation,         // s
    $age,              // i
    $gender,           // s
    $phone,            // s
    $blood_group,      // s
    $address,          // s
    $medical_history,  // s
    $allergies,        // s
    $medications,      // s
    $id                // i
);

/* ---------- EXECUTE ---------- */
if ($stmt->execute()) {
    $stmt->close();
    $conn->close();
    sendResponse(true, "Family member updated successfully");
} else {
    $errorMsg = $stmt->error ?: "Unknown database error";
    $stmt->close();
    $conn->close();
    sendResponse(false, "Database error: " . $errorMsg, 500);
}

