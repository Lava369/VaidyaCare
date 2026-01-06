<?php
/**
 * Update Doctor Personal Information API
 * POST: update_doctor_personal_info.php
 * Body: JSON { "doctor_id": 1, "full_name": "...", ... }
 * Response: { "success": true/false, "message": "..." }
 */

header("Content-Type: application/json; charset=UTF-8");
require_once "config.php";

/* ---------- ONLY POST ---------- */
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    sendResponse(false, "Only POST method allowed", null, 405);
}

/* ---------- GET JSON INPUT ---------- */
$json = file_get_contents('php://input');
$data = json_decode($json, true);

if (!$data) {
    sendResponse(false, "Invalid JSON data", null, 400);
}

/* ---------- VALIDATE DOCTOR ID ---------- */
$doctorId = isset($data['doctor_id']) ? intval($data['doctor_id']) : 0;

if ($doctorId <= 0) {
    sendResponse(false, "Invalid doctor ID", null, 400);
}

/* ---------- SANITIZE INPUT ---------- */
$fullName = isset($data['full_name']) ? trim($data['full_name']) : '';
$email = isset($data['email']) ? trim($data['email']) : '';
$phone = isset($data['phone']) ? trim($data['phone']) : '';
$location = isset($data['location']) ? trim($data['location']) : '';
$birthMonth = isset($data['birth_month']) ? trim($data['birth_month']) : 'January';
$gender = isset($data['gender']) ? trim($data['gender']) : 'Male';

/* ---------- VALIDATE REQUIRED FIELDS ---------- */
if (empty($fullName) || empty($email) || empty($phone)) {
    sendResponse(false, "Full name, email, and phone are required", null, 400);
}

/* ---------- VALIDATE EMAIL ---------- */
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    sendResponse(false, "Invalid email format", null, 400);
}

/* ---------- VALIDATE GENDER ---------- */
$allowedGenders = ['Male', 'Female', 'Other'];
if (!in_array($gender, $allowedGenders)) {
    $gender = 'Male';
}

/* ---------- DB CONNECTION ---------- */
$conn = getDBConnectionSafe();
if (!$conn) {
    sendResponse(false, "Database connection failed", null, 500);
}

/* ---------- CHECK IF DOCTOR EXISTS ---------- */
$checkStmt = $conn->prepare("SELECT doctor_id FROM doctors WHERE doctor_id = ?");
$checkStmt->bind_param("i", $doctorId);
$checkStmt->execute();
$result = $checkStmt->get_result();

if ($result->num_rows === 0) {
    $checkStmt->close();
    $conn->close();
    sendResponse(false, "Doctor not found", null, 404);
}
$checkStmt->close();

/* ---------- CHECK EMAIL UNIQUENESS (if changed) ---------- */
$emailCheckStmt = $conn->prepare("SELECT doctor_id FROM doctors WHERE email = ? AND doctor_id != ?");
$emailCheckStmt->bind_param("si", $email, $doctorId);
$emailCheckStmt->execute();
$emailResult = $emailCheckStmt->get_result();

if ($emailResult->num_rows > 0) {
    $emailCheckStmt->close();
    $conn->close();
    sendResponse(false, "Email already exists", null, 400);
}
$emailCheckStmt->close();

/* ---------- UPDATE PERSONAL INFO ---------- */
$stmt = $conn->prepare("
    UPDATE doctors SET
        full_name = ?,
        email = ?,
        mobile = ?,
        location = ?,
        birth_month = ?,
        gender = ?,
        updated_at = NOW()
    WHERE doctor_id = ?
");

$stmt->bind_param(
    "ssssssi",
    $fullName,
    $email,
    $phone,
    $location,
    $birthMonth,
    $gender,
    $doctorId
);

if ($stmt->execute()) {
    $stmt->close();
    $conn->close();
    sendResponse(true, "Personal information updated successfully", null, 200);
} else {
    $stmt->close();
    $conn->close();
    sendResponse(false, "Failed to update personal information", null, 500);
}
?>

