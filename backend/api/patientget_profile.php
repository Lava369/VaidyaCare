<?php
/**
 * Get Patient Profile API
 * GET: patientget_profile.php?email=user@example.com OR ?user_id=1
 * Response: { "success": true, "data": {...} }
 */

header("Content-Type: application/json; charset=UTF-8");
require_once "config.php";

/* ---------- ONLY GET ---------- */
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    sendResponse(false, "Only GET method allowed", null, 405);
}

/* ---------- GET PARAMETERS ---------- */
$email = isset($_GET['email']) ? trim($_GET['email']) : '';
$userId = isset($_GET['user_id']) ? intval($_GET['user_id']) : 0;

if (empty($email) && $userId <= 0) {
    sendResponse(false, "Email or user_id required", null, 400);
}

/* ---------- DB CONNECTION ---------- */
$conn = getDBConnectionSafe();
if (!$conn) {
    sendResponse(false, "Database connection failed", null, 500);
}

/* ---------- BUILD QUERY ---------- */
// Use users table (same as signup)
if ($userId > 0) {
    $stmt = $conn->prepare("
        SELECT 
            id AS user_id,
            full_name,
            email,
            mobile,
            age,
            gender,
            dob,
            blood_group,
            height_cm,
            weight_kg,
            address,
            city,
            state,
            pin,
            patient_id
        FROM users 
        WHERE id = ?
    ");
    $stmt->bind_param("i", $userId);
} else {
    $stmt = $conn->prepare("
        SELECT 
            id AS user_id,
            full_name,
            email,
            mobile,
            age,
            gender,
            dob,
            blood_group,
            height_cm,
            weight_kg,
            address,
            city,
            state,
            pin,
            patient_id
        FROM users 
        WHERE email = ?
    ");
    $stmt->bind_param("s", $email);
}

$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    $stmt->close();
    $conn->close();
    sendResponse(false, "Patient not found", null, 404);
}

$patient = $result->fetch_assoc();
$stmt->close();
$conn->close();

// Format patient ID
$patientId = !empty($patient['patient_id']) ? $patient['patient_id'] : 'VD' . str_pad($patient['user_id'], 5, '0', STR_PAD_LEFT);

sendResponse(true, "Profile retrieved successfully", [
    'full_name' => $patient['full_name'] ?? '',
    'email' => $patient['email'] ?? '',
    'mobile' => $patient['mobile'] ?? '',
    'age' => $patient['age'] ?? '',
    'gender' => $patient['gender'] ?? '',
    'dob' => $patient['dob'] ?? '',
    'blood_group' => $patient['blood_group'] ?? '',
    'height_cm' => $patient['height_cm'] ?? '',
    'weight_kg' => $patient['weight_kg'] ?? '',
    'address' => $patient['address'] ?? '',
    'city' => $patient['city'] ?? '',
    'state' => $patient['state'] ?? '',
    'pin' => $patient['pin'] ?? '',
    'patient_id' => $patientId
], 200);
?>
