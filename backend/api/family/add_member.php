<?php
/**
 * Add Family Member API
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

    $res = [
        "success" => $success,
        "message" => $message
    ];

    if ($data !== null) {
        $res["data"] = $data;
    }

    echo json_encode($res, JSON_UNESCAPED_UNICODE);
    exit;
}

/* ---------- ONLY POST ---------- */
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    sendResponse(false, "Only POST method allowed", null, 405);
}

/* ---------- READ JSON ---------- */
$data = json_decode(file_get_contents("php://input"), true);
if (!is_array($data)) {
    sendResponse(false, "Invalid JSON input", null, 400);
}

/* ---------- REQUIRED ---------- */
$user_id  = (int)($data['user_id'] ?? 0);
$name     = trim($data['name'] ?? '');
$relation = trim($data['relation'] ?? '');
$age      = (int)($data['age'] ?? 0);

if ($user_id <= 0 || $name === '' || $relation === '' || $age <= 0) {
    sendResponse(false, "Required fields missing or invalid", null, 400);
}

/* ---------- OPTIONAL ---------- */
$gender           = trim($data['gender'] ?? '') ?: null;
$phone            = trim($data['phone'] ?? '') ?: null;
$blood_group      = trim($data['blood_group'] ?? '') ?: null;
$address          = trim($data['address'] ?? '') ?: null;
$medical_history  = trim($data['medical_history'] ?? '') ?: null;
$allergies        = trim($data['allergies'] ?? '') ?: null;
$medications      = trim($data['medications'] ?? '') ?: null;

/* ---------- DB ---------- */
$conn = getDBConnectionSafe();
if (!$conn) {
    sendResponse(false, "Database connection failed", null, 500);
}

/* ---------- INSERT ---------- */
$stmt = $conn->prepare("
    INSERT INTO family_members (
        user_id, name, relation, age,
        gender, phone, blood_group, address,
        medical_history, allergies, medications
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
");

if (!$stmt) {
    sendResponse(false, "SQL prepare failed", null, 500);
}

$stmt->bind_param(
    "ississsssss",
    $user_id,
    $name,
    $relation,
    $age,
    $gender,
    $phone,
    $blood_group,
    $address,
    $medical_history,
    $allergies,
    $medications
);

if ($stmt->execute()) {
    $newId = $stmt->insert_id;
    $stmt->close();
    $conn->close();

    sendResponse(true, "Family member added successfully", [
        "id" => $newId
    ]);
} else {
    $err = $stmt->error ?: "Database error";
    $stmt->close();
    $conn->close();
    sendResponse(false, $err, null, 500);
}
