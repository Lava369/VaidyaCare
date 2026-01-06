<?php
// File: C:\xampp\htdocs\VaidyaCare\api\reject_doctor.php

require_once 'config.php';

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    sendResponse(false, 'Method not allowed');
}

// Get JSON or form data
$json = file_get_contents('php://input');
$data = json_decode($json, true);

// Support both JSON and form-urlencoded
if (!$data) {
    $data = $_POST;
}

$doctor_id = isset($data['doctor_id']) ? (int)$data['doctor_id'] : 0;

if ($doctor_id <= 0) {
    sendResponse(false, 'Invalid doctor ID');
}

// Connect to database
$conn = getDBConnection();

// Check if doctor exists and is pending
$stmt = $conn->prepare("SELECT id, status, license_cert_path, degree_cert_path, id_proof_path FROM doctors WHERE id = ?");
$stmt->bind_param("i", $doctor_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    $stmt->close();
    $conn->close();
    sendResponse(false, 'Doctor not found');
}

$doctor = $result->fetch_assoc();

if ($doctor['status'] !== 'pending') {
    $stmt->close();
    $conn->close();
    sendResponse(false, 'Doctor is not in pending status');
}

// Delete uploaded files
if (!empty($doctor['license_cert_path'])) {
    @unlink(__DIR__ . '/' . $doctor['license_cert_path']);
}
if (!empty($doctor['degree_cert_path'])) {
    @unlink(__DIR__ . '/' . $doctor['degree_cert_path']);
}
if (!empty($doctor['id_proof_path'])) {
    @unlink(__DIR__ . '/' . $doctor['id_proof_path']);
}

// Update doctor status to rejected
$stmt = $conn->prepare("UPDATE doctors SET status = 'rejected' WHERE id = ?");
$stmt->bind_param("i", $doctor_id);

if ($stmt->execute()) {
    $stmt->close();
    $conn->close();
    sendResponse(true, 'Doctor rejected successfully');
} else {
    $error = $stmt->error;
    $stmt->close();
    $conn->close();
    sendResponse(false, 'Failed to reject doctor: ' . $error);
}
?>