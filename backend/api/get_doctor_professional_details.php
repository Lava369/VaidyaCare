<?php
/**
 * Get Doctor Professional Details API
 * GET: get_doctor_professional_details.php?doctor_id=1
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

/* ---------- FETCH PROFESSIONAL DETAILS ---------- */
$stmt = $conn->prepare("
    SELECT 
        doctor_id,
        full_name,
        doctor_mode,
        primary_specialization,
        sub_specialization,
        experience_years,
        license_no,
        medical_council,
        qualifications,
        languages_spoken,
        consultation_fee
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
sendResponse(true, "Professional details retrieved successfully", [
    'doctor_id' => $doctor['doctor_id'],
    'full_name' => $doctor['full_name'] ?? '',
    'doctor_mode' => $doctor['doctor_mode'] ?? 'Offline',
    'primary_specialization' => $doctor['primary_specialization'] ?? '',
    'sub_specialization' => $doctor['sub_specialization'] ?? '',
    'experience_years' => $doctor['experience_years'] ?? '',
    'medical_license_number' => $doctor['license_no'] ?? '',
    'medical_council' => $doctor['medical_council'] ?? '',
    'qualifications' => $doctor['qualifications'] ?? '',
    'languages_spoken' => $doctor['languages_spoken'] ?? '',
    'consultation_fee' => $doctor['consultation_fee'] ?? '0'
], 200);
?>

