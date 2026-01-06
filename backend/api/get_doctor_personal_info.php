<?php
/**
 * Get Doctor Personal Information API
 * GET: get_doctor_personal_info.php?doctor_id=1
 * Response: { "success": true, "data": {...} }
 */

header("Content-Type: application/json; charset=UTF-8");
require_once "config.php";

/* ---------- ONLY GET ---------- */
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    sendResponse(false, "Only GET method allowed", null, 405);
}

/* ---------- GET DOCTOR ID ---------- */
$doctorId = isset($_GET['doctor_id']) ? intval($_GET['doctor_id']) : 0;

if ($doctorId <= 0) {
    sendResponse(false, "Invalid doctor ID", null, 400);
}

/* ---------- DB CONNECTION ---------- */
$conn = getDBConnectionSafe();
if (!$conn) {
    sendResponse(false, "Database connection failed", null, 500);
}

/* ---------- FETCH PERSONAL INFO ---------- */
$stmt = $conn->prepare("
    SELECT 
        doctor_id,
        full_name,
        email,
        mobile,
        location,
        birth_month,
        gender,
        profile_image
    FROM doctors 
    WHERE doctor_id = ?
");

$stmt->bind_param("i", $doctorId);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    $stmt->close();
    $conn->close();
    sendResponse(false, "Doctor not found", null, 404);
}

$doctor = $result->fetch_assoc();
$stmt->close();
$conn->close();

/* ---------- RETURN DATA ---------- */
sendResponse(true, "Personal information retrieved successfully", [
    'doctor_id' => $doctor['doctor_id'],
    'full_name' => $doctor['full_name'] ?? '',
    'email' => $doctor['email'] ?? '',
    'phone' => $doctor['mobile'] ?? '',
    'location' => $doctor['location'] ?? '',
    'birth_month' => $doctor['birth_month'] ?? 'January',
    'gender' => $doctor['gender'] ?? 'Male',
    'profile_image' => $doctor['profile_image'] ?? null
], 200);
?>

