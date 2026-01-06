<?php
/**
 * Update Doctor Edit Profile API
 * POST: update_doctor_edit_profile.php
 * Body: JSON { "doctor_id": 1, "display_name": "...", "bio": "...", ... }
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
$displayName = isset($data['display_name']) ? trim($data['display_name']) : '';
$dateOfBirth = isset($data['date_of_birth']) ? trim($data['date_of_birth']) : '';
$bio = isset($data['bio']) ? trim($data['bio']) : '';
$clinicName = isset($data['clinic_name']) ? trim($data['clinic_name']) : '';
$clinicAddress = isset($data['clinic_address']) ? trim($data['clinic_address']) : '';

/* ---------- VALIDATE DATE FORMAT ---------- */
if (!empty($dateOfBirth)) {
    // Validate date format (YYYY-MM-DD)
    if (!preg_match('/^\d{4}-\d{2}-\d{2}$/', $dateOfBirth)) {
        sendResponse(false, "Invalid date format. Use YYYY-MM-DD", null, 400);
    }
    // Validate date is valid
    $dateParts = explode('-', $dateOfBirth);
    if (!checkdate((int)$dateParts[1], (int)$dateParts[2], (int)$dateParts[0])) {
        sendResponse(false, "Invalid date", null, 400);
    }
}

/* ---------- VALIDATE REQUIRED FIELDS ---------- */
if (empty($displayName)) {
    sendResponse(false, "Display name is required", null, 400);
}

/* ---------- VALIDATE BIO LENGTH ---------- */
if (strlen($bio) > 500) {
    sendResponse(false, "Bio must be 500 characters or less", null, 400);
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

/* ---------- UPDATE EDIT PROFILE ---------- */
// Check if date_of_birth column exists
$checkColumn = $conn->query("SHOW COLUMNS FROM doctors LIKE 'date_of_birth'");
$hasDateOfBirthColumn = $checkColumn && $checkColumn->num_rows > 0;

if ($hasDateOfBirthColumn && !empty($dateOfBirth)) {
    $stmt = $conn->prepare("
        UPDATE doctors SET
            display_name = ?,
            date_of_birth = ?,
            bio = ?,
            clinic_name = ?,
            clinic_address = ?,
            updated_at = NOW()
        WHERE doctor_id = ?
    ");
    $stmt->bind_param(
        "sssssi",
        $displayName,
        $dateOfBirth,
        $bio,
        $clinicName,
        $clinicAddress,
        $doctorId
    );
} else if ($hasDateOfBirthColumn && empty($dateOfBirth)) {
    // Update with NULL date_of_birth
    $stmt = $conn->prepare("
        UPDATE doctors SET
            display_name = ?,
            date_of_birth = NULL,
            bio = ?,
            clinic_name = ?,
            clinic_address = ?,
            updated_at = NOW()
        WHERE doctor_id = ?
    ");
    $stmt->bind_param(
        "ssssi",
        $displayName,
        $bio,
        $clinicName,
        $clinicAddress,
        $doctorId
    );
} else {
    // Column doesn't exist, update without date_of_birth
    $stmt = $conn->prepare("
        UPDATE doctors SET
            display_name = ?,
            bio = ?,
            clinic_name = ?,
            clinic_address = ?,
            updated_at = NOW()
        WHERE doctor_id = ?
    ");
    $stmt->bind_param(
        "ssssi",
        $displayName,
        $bio,
        $clinicName,
        $clinicAddress,
        $doctorId
    );
}

if ($stmt->execute()) {
    $stmt->close();
    $conn->close();
    sendResponse(true, "Profile updated successfully", null, 200);
} else {
    $errorMsg = $stmt->error ?: "Failed to update profile";
    $stmt->close();
    $conn->close();
    sendResponse(false, "Database error: " . $errorMsg, null, 500);
}
?>

