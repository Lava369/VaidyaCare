<?php
/**
 * Test Doctor Data API - For debugging
 * GET: test_doctor_data.php?doctor_id=1
 * Response: Shows all doctor data including professional details
 */

header("Content-Type: application/json; charset=UTF-8");
require_once "config.php";

$doctorId = isset($_GET['doctor_id']) ? intval($_GET['doctor_id']) : 0;

if ($doctorId <= 0) {
    sendResponse(false, "Invalid doctor ID", null, 400);
}

$conn = getDBConnectionSafe();
if (!$conn) {
    sendResponse(false, "Database connection failed", null, 500);
}

$stmt = $conn->prepare("
    SELECT 
        doctor_id,
        full_name,
        display_name,
        primary_specialization,
        sub_specialization,
        experience_years,
        consultation_fee,
        doctor_mode,
        status,
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

sendResponse(true, "Doctor data retrieved", $doctor, 200);
?>

