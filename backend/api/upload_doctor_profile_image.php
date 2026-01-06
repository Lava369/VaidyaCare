<?php
/**
 * Upload Doctor Profile Image API
 * POST: upload_doctor_profile_image.php
 * Body: multipart/form-data with doctor_id and profile_image
 * Response: { "success": true/false, "message": "...", "image_url": "..." }
 */

header("Content-Type: application/json; charset=UTF-8");
require_once "config.php";

/* ---------- ONLY POST ---------- */
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    sendResponse(false, "Only POST method allowed", null, 405);
}

/* ---------- GET DOCTOR ID ---------- */
$doctorId = isset($_POST['doctor_id']) ? intval($_POST['doctor_id']) : 0;

if ($doctorId <= 0) {
    sendResponse(false, "Invalid doctor ID", null, 400);
}

/* ---------- CHECK IF IMAGE UPLOADED ---------- */
if (!isset($_FILES['profile_image']) || $_FILES['profile_image']['error'] !== UPLOAD_ERR_OK) {
    sendResponse(false, "No image file uploaded", null, 400);
}

$file = $_FILES['profile_image'];

/* ---------- VALIDATE FILE TYPE ---------- */
$allowedTypes = ['image/jpeg', 'image/jpg', 'image/png'];
$fileType = $file['type'];

if (!in_array($fileType, $allowedTypes)) {
    sendResponse(false, "Invalid file type. Only JPEG and PNG images are allowed", null, 400);
}

/* ---------- VALIDATE FILE SIZE (max 5MB) ---------- */
$maxSize = 5 * 1024 * 1024; // 5MB
if ($file['size'] > $maxSize) {
    sendResponse(false, "File size too large. Maximum size is 5MB", null, 400);
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

/* ---------- CREATE UPLOAD DIRECTORY ---------- */
$uploadDir = dirname(__DIR__) . "/uploads/doctors/profile_images/";
if (!file_exists($uploadDir)) {
    if (!mkdir($uploadDir, 0755, true)) {
        $conn->close();
        sendResponse(false, "Failed to create upload directory", null, 500);
    }
}

/* ---------- GENERATE UNIQUE FILENAME ---------- */
$extension = pathinfo($file['name'], PATHINFO_EXTENSION);
$fileName = "doctor_" . $doctorId . "_" . time() . "." . $extension;
$filePath = $uploadDir . $fileName;

/* ---------- MOVE UPLOADED FILE ---------- */
if (!move_uploaded_file($file['tmp_name'], $filePath)) {
    $conn->close();
    sendResponse(false, "Failed to upload image", null, 500);
}

/* ---------- SAVE IMAGE PATH TO DATABASE ---------- */
$imageUrl = "/vaidyacare/uploads/doctors/profile_images/" . $fileName;

$stmt = $conn->prepare("UPDATE doctors SET profile_image = ?, updated_at = NOW() WHERE doctor_id = ?");
$stmt->bind_param("si", $imageUrl, $doctorId);

if ($stmt->execute()) {
    $stmt->close();
    $conn->close();
    sendResponse(true, "Profile image uploaded successfully", ['image_url' => $imageUrl], 200);
} else {
    // Delete uploaded file if database update fails
    @unlink($filePath);
    $stmt->close();
    $conn->close();
    sendResponse(false, "Failed to save image path to database", null, 500);
}
?>

