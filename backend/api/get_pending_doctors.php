<?php
ob_start();
header("Content-Type: application/json; charset=UTF-8");
require_once 'config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    ob_clean();
    echo json_encode([
        "success" => false,
        "message" => "Method not allowed"
    ]);
    exit;
}

$conn = getDBConnection();

$sql = "
    SELECT
        doctor_id,
        full_name,
        specialization,
        experience_years,
        clinic_name,
        registration_number
    FROM doctors
    WHERE verification_status = 'pending'
    ORDER BY doctor_id DESC
";

$stmt = $conn->prepare($sql);
$stmt->execute();
$result = $stmt->get_result();

$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = [
        "doctor_id" => (int)$row["doctor_id"],
        "full_name" => $row["full_name"],
        "specialization" => $row["specialization"] ?? "Not specified",
        "experience_years" => (int)($row["experience_years"] ?? 0),
        "clinic_name" => $row["clinic_name"] ?? "Not specified",
        "registration_number" => $row["registration_number"] ?? ""
    ];
}

$stmt->close();
$conn->close();

ob_clean();
echo json_encode([
    "success" => true,
    "data" => $data
]);
exit;
