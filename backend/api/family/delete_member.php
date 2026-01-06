<?php
/**
 * Delete Family Member API
 * POST: delete_member.php
 * Body: JSON { "id": 1 }
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

/* ---------- GET MEMBER ID ---------- */
$id = isset($data['id']) ? intval($data['id']) : 0;

if ($id <= 0) {
    sendResponse(false, "Invalid member ID", 400);
}

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

/* ---------- DELETE FAMILY MEMBER ---------- */
$stmt = $conn->prepare("DELETE FROM family_members WHERE id = ?");

if (!$stmt) {
    $error = $conn->error ?: "Unknown SQL error";
    $conn->close();
    sendResponse(false, "SQL prepare failed: " . $error, 500);
}

$stmt->bind_param("i", $id);

/* ---------- EXECUTE ---------- */
if ($stmt->execute()) {
    $stmt->close();
    $conn->close();
    sendResponse(true, "Family member deleted successfully");
} else {
    $errorMsg = $stmt->error ?: "Unknown database error";
    $stmt->close();
    $conn->close();
    sendResponse(false, "Database error: " . $errorMsg, 500);
}

