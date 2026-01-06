<?php
ob_start();
header("Content-Type: application/json; charset=UTF-8");
require_once 'config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    ob_clean();
    echo json_encode([
        "success" => false,
        "message" => "Method not allowed"
    ]);
    exit;
}

$doctorId = (int)($_POST["doctor_id"] ?? 0);

if ($doctorId <= 0) {
    ob_clean();
    echo json_encode([
        "success" => false,
        "message" => "Invalid doctor ID"
    ]);
    exit;
}

$conn = getDBConnection();

$stmt = $conn->prepare("
    UPDATE doctors
    SET verification_status = 'approved'
    WHERE doctor_id = ? AND verification_status = 'pending'
");
$stmt->bind_param("i", $doctorId);

$success = $stmt->execute();

$stmt->close();
$conn->close();

ob_clean();
echo json_encode([
    "success" => $success,
    "message" => $success
        ? "Doctor approved successfully"
        : "Approval failed"
]);
exit;
