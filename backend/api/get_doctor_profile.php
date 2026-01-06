<?php
/**
 * Get Doctor Profile API
 * GET: get_doctor_profile.php?doctor_id=1
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

/* ---------- FETCH DOCTOR PROFILE ---------- */
$stmt = $conn->prepare("
    SELECT 
        doctor_id,
        full_name,
        email,
        mobile,
        license_no,
        primary_specialization,
        verification_status,
        profile_image,
        created_at
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

/* ---------- GET INITIALS FOR AVATAR ---------- */
$initials = "";
if (!empty($doctor['full_name'])) {
    $names = explode(" ", $doctor['full_name']);
    if (count($names) >= 2) {
        $initials = strtoupper(substr($names[0], 0, 1) . substr($names[count($names) - 1], 0, 1));
    } else {
        $initials = strtoupper(substr($doctor['full_name'], 0, 2));
    }
}

/* ---------- RETURN DATA ---------- */
sendResponse(true, "Profile retrieved successfully", [
    'doctor_id' => $doctor['doctor_id'],
    'full_name' => $doctor['full_name'] ?? '',
    'email' => $doctor['email'] ?? '',
    'mobile' => $doctor['mobile'] ?? '',
    'license_no' => $doctor['license_no'] ?? '',
    'specialization' => $doctor['primary_specialization'] ?? 'General Medicine',
    'verification_status' => $doctor['verification_status'] ?? 'pending',
    'initials' => $initials,
    'profile_image' => $doctor['profile_image'] ?? '',
    'created_at' => $doctor['created_at'] ?? ''
], 200);
?>

