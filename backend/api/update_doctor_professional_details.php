<?php
/**
 * Update Doctor Professional Details API
 * POST: update_doctor_professional_details.php
 * Body: JSON { "doctor_id": 1, "doctor_mode": "Online", ... }
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
$doctorMode = isset($data['doctor_mode']) ? trim($data['doctor_mode']) : 'Offline';
$primarySpecialization = isset($data['primary_specialization']) ? trim($data['primary_specialization']) : '';
$subSpecialization = isset($data['sub_specialization']) ? trim($data['sub_specialization']) : '';
$experienceYears = isset($data['experience_years']) ? trim($data['experience_years']) : '';
$medicalLicenseNumber = isset($data['medical_license_number']) ? trim($data['medical_license_number']) : '';
$medicalCouncil = isset($data['medical_council']) ? trim($data['medical_council']) : '';
$qualifications = isset($data['qualifications']) ? trim($data['qualifications']) : '';
$languagesSpoken = isset($data['languages_spoken']) ? trim($data['languages_spoken']) : '';
$consultationFee = isset($data['consultation_fee']) ? floatval($data['consultation_fee']) : 0;

/* ---------- VALIDATE DOCTOR MODE ---------- */
$allowedModes = ['Online', 'Offline', 'Busy'];
if (!in_array($doctorMode, $allowedModes)) {
    $doctorMode = 'Offline';
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

/* ---------- UPDATE PROFESSIONAL DETAILS ---------- */
$stmt = $conn->prepare("
    UPDATE doctors SET
        full_name = ?,
        doctor_mode = ?,
        primary_specialization = ?,
        sub_specialization = ?,
        experience_years = ?,
        license_no = ?,
        medical_council = ?,
        qualifications = ?,
        languages_spoken = ?,
        consultation_fee = ?,
        updated_at = NOW()
    WHERE doctor_id = ?
");

$stmt->bind_param(
    "sssssssssdi",
    $fullName,
    $doctorMode,
    $primarySpecialization,
    $subSpecialization,
    $experienceYears,
    $medicalLicenseNumber,
    $medicalCouncil,
    $qualifications,
    $languagesSpoken,
    $consultationFee,
    $doctorId
);

if ($stmt->execute()) {
    // Verify the update was successful by fetching the updated data
    $verifyStmt = $conn->prepare("
        SELECT 
            full_name,
            doctor_mode,
            primary_specialization,
            experience_years,
            consultation_fee
        FROM doctors 
        WHERE doctor_id = ?
    ");
    $verifyStmt->bind_param("i", $doctorId);
    $verifyStmt->execute();
    $verifyResult = $verifyStmt->get_result();
    $updatedData = $verifyResult->fetch_assoc();
    $verifyStmt->close();
    
    $stmt->close();
    $conn->close();
    
    sendResponse(true, "Professional details updated successfully", [
        'full_name' => $updatedData['full_name'] ?? '',
        'doctor_mode' => $updatedData['doctor_mode'] ?? 'Offline',
        'primary_specialization' => $updatedData['primary_specialization'] ?? '',
        'experience_years' => $updatedData['experience_years'] ?? '',
        'consultation_fee' => $updatedData['consultation_fee'] ?? 0
    ], 200);
} else {
    $errorMsg = $stmt->error ?: "Failed to update professional details";
    $stmt->close();
    $conn->close();
    sendResponse(false, "Database error: " . $errorMsg, null, 500);
}
?>

