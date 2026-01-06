<?php
/**
 * Get Family Members API
 * GET: get_members.php?user_id=1
 * Response: { "success": true, "data": [...] }
 */

ob_start();
error_reporting(0);
ini_set('display_errors', 0);

header("Content-Type: application/json; charset=UTF-8");

require_once "../config.php";

/* ---------- RESPONSE HELPER ---------- */
function sendResponse($success, $message, $data = null, $code = 200) {
    http_response_code($code);
    ob_clean();
    echo json_encode([
        "success" => $success,
        "message" => $message,
        "data" => $data
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

/* ---------- ONLY GET ---------- */
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    sendResponse(false, "Only GET method allowed", null, 405);
}

/* ---------- GET USER ID ---------- */
$user_id = isset($_GET['user_id']) ? intval($_GET['user_id']) : 0;

if ($user_id <= 0) {
    sendResponse(false, "Invalid user_id", null, 400);
}

/* ---------- DB CONNECTION ---------- */
$conn = getDBConnectionSafe();
if (!$conn) {
    sendResponse(false, "Database connection failed", null, 500);
}

/* ---------- FETCH FAMILY MEMBERS ---------- */
$stmt = $conn->prepare("
    SELECT 
        id,
        name,
        relation,
        age,
        gender,
        phone,
        blood_group,
        address,
        medical_history,
        allergies,
        medications
    FROM family_members
    WHERE user_id = ?
    ORDER BY created_at DESC
");

if (!$stmt) {
    $conn->close();
    sendResponse(false, "SQL prepare failed: " . $conn->error, null, 500);
}

$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$members = [];
while ($row = $result->fetch_assoc()) {
    $members[] = [
        "id" => intval($row['id']),
        "name" => $row['name'],
        "relation" => $row['relation'],
        "age" => intval($row['age']),
        "gender" => $row['gender'] ?? "",
        "phone" => $row['phone'] ?? "",
        "blood_group" => $row['blood_group'] ?? "",
        "address" => $row['address'] ?? "",
        "medical_history" => $row['medical_history'] ?? "",
        "allergies" => $row['allergies'] ?? "",
        "medications" => $row['medications'] ?? ""
    ];
}

$stmt->close();
$conn->close();

sendResponse(true, "Family members retrieved successfully", $members);

