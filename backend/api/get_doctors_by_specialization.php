<?php
/**
 * Get Doctors by Specialization API
 * GET: get_doctors_by_specialization.php?specialization=Cardiologist
 * Response: { "success": true, "data": [...] }
 */

header("Content-Type: application/json; charset=UTF-8");
require_once "config.php";

/* ---------- ONLY GET ---------- */
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    sendResponse(false, "Only GET method allowed", null, 405);
}

/* ---------- GET SPECIALIZATION ---------- */
$specialization = isset($_GET['specialization']) ? trim($_GET['specialization']) : '';

if (empty($specialization)) {
    sendResponse(false, "Specialization parameter is required", null, 400);
}

/* ---------- DB CONNECTION ---------- */
$conn = getDBConnectionSafe();
if (!$conn) {
    sendResponse(false, "Database connection failed", null, 500);
}

/* ---------- FETCH DOCTORS BY SPECIALIZATION ---------- */
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
        profile_image,
        clinic_name,
        clinic_address
    FROM doctors 
    WHERE status = 'approved' 
    AND (primary_specialization = ? OR sub_specialization = ?)
    ORDER BY full_name ASC
");

$stmt->bind_param("ss", $specialization, $specialization);
$stmt->execute();
$result = $stmt->get_result();

$doctors = [];
while ($row = $result->fetch_assoc()) {
    // Calculate default rating (can be replaced with actual ratings later)
    $rating = 4.5 + (rand(0, 5) / 10); // Random between 4.5 and 5.0
    
    // Parse experience_years - handle both string and numeric values
    $experienceYears = 0;
    if (!empty($row['experience_years'])) {
        $expStr = trim($row['experience_years']);
        // Extract numeric value from string (e.g., "5 years" -> 5)
        if (preg_match('/(\d+)/', $expStr, $matches)) {
            $experienceYears = (int)$matches[1];
        } else {
            $experienceYears = (int)$expStr;
        }
    }
    
    // Parse consultation_fee
    $consultationFee = 0;
    if (!empty($row['consultation_fee'])) {
        $consultationFee = (int)round((float)$row['consultation_fee']);
    }
    
    $doctors[] = [
        'doctor_id' => (int)$row['doctor_id'],
        'name' => !empty($row['display_name']) ? $row['display_name'] : $row['full_name'],
        'specialization' => !empty($row['primary_specialization']) ? $row['primary_specialization'] : 'General Medicine',
        'experience' => $experienceYears,
        'fee' => $consultationFee,
        'rating' => round($rating, 1),
        'status' => !empty($row['doctor_mode']) ? $row['doctor_mode'] : 'Offline',
        'profile_image' => $row['profile_image'] ?? '',
        'clinic_name' => $row['clinic_name'] ?? '',
        'clinic_address' => $row['clinic_address'] ?? '',
        'sub_specialization' => $row['sub_specialization'] ?? ''
    ];
}

$stmt->close();
$conn->close();

sendResponse(true, "Doctors retrieved successfully", $doctors, 200);
?>

