<?php
/**
 * Doctor Signup API
 * URL: /vaidyacare/api/doctor_signup.php
 */

ob_start();
error_reporting(0);
ini_set('display_errors', 0);

header("Content-Type: application/json; charset=UTF-8");
require_once "config.php";

/* ---------- API RESPONSE HELPER ---------- */
function apiResponse($success, $message, $data = null, $code = 200) {
    http_response_code($code);
    ob_clean();

    $res = [
        "success" => $success,
        "message" => $message
    ];

    if ($data !== null) {
        $res["data"] = $data;
    }

    echo json_encode($res, JSON_UNESCAPED_UNICODE);
    exit;
}

/* ---------- ONLY POST ---------- */
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    apiResponse(false, "Only POST method allowed", null, 405);
}

/* ---------- MULTIPART CHECK ---------- */
if (empty($_POST) || empty($_FILES)) {
    apiResponse(false, "Invalid request format. Use multipart/form-data.", null, 400);
}

/* ---------- REQUIRED FIELDS ---------- */
$requiredFields = ['full_name', 'email', 'mobile', 'license_no', 'password'];
foreach ($requiredFields as $field) {
    if (!isset($_POST[$field]) || trim($_POST[$field]) === '') {
        apiResponse(false, ucfirst(str_replace('_',' ',$field)) . " is required", null, 400);
    }
}

/* ---------- REQUIRED FILES ---------- */
$requiredFiles = ['license_cert', 'degree_cert', 'id_proof'];
foreach ($requiredFiles as $file) {
    if (!isset($_FILES[$file]) || $_FILES[$file]['error'] !== UPLOAD_ERR_OK) {
        apiResponse(false, ucfirst(str_replace('_',' ',$file)) . " is required", null, 400);
    }
}

/* ---------- SANITIZE INPUT ---------- */
$fullName  = trim($_POST['full_name']);
$email     = strtolower(trim($_POST['email']));
$mobile    = trim($_POST['mobile']);
$licenseNo = trim($_POST['license_no']);
$password  = $_POST['password'];

/* ---------- VALIDATION ---------- */
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    apiResponse(false, "Invalid email format", null, 400);
}

if (!preg_match('/^\d{10}$/', $mobile)) {
    apiResponse(false, "Invalid mobile number", null, 400);
}

if (strlen($password) < 6) {
    apiResponse(false, "Password must be at least 6 characters", null, 400);
}

/* ---------- DB CONNECTION (SAFE) ---------- */
$conn = getDBConnectionSafe();
if (!$conn) {
    apiResponse(false, "Database connection failed", null, 500);
}

/* ---------- DUPLICATE CHECK ---------- */
$stmt = $conn->prepare("
    SELECT doctor_id 
    FROM doctors 
    WHERE email = ? OR mobile = ? OR license_no = ?
");
$stmt->bind_param("sss", $email, $mobile, $licenseNo);
$stmt->execute();
$stmt->store_result();

if ($stmt->num_rows > 0) {
    $stmt->close();
    $conn->close();
    apiResponse(false, "Doctor already registered", null, 409);
}
$stmt->close();

/* ---------- UPLOAD DIRECTORY ---------- */
$uploadDir = dirname(__DIR__) . "/uploads/doctors/";
if (!is_dir($uploadDir)) {
    mkdir($uploadDir, 0755, true);
}

/* ---------- FILE UPLOAD FUNCTION ---------- */
function uploadFile($file, $prefix, $dir) {
    $ext  = strtolower(pathinfo($file['name'], PATHINFO_EXTENSION));
    $name = $prefix . "_" . time() . "_" . uniqid() . "." . $ext;
    $path = $dir . $name;

    if (!move_uploaded_file($file['tmp_name'], $path)) {
        apiResponse(false, "File upload failed", null, 500);
    }

    return "uploads/doctors/" . $name;
}

/* ---------- UPLOAD FILES ---------- */
$licensePath = uploadFile($_FILES['license_cert'], "license", $uploadDir);
$degreePath  = uploadFile($_FILES['degree_cert'],  "degree",  $uploadDir);
$idPath      = uploadFile($_FILES['id_proof'],      "id",      $uploadDir);

/* ---------- INSERT DOCTOR ---------- */
$hashedPassword     = password_hash($password, PASSWORD_DEFAULT);
$verificationStatus = "pending";

$stmt = $conn->prepare("
INSERT INTO doctors
(
    full_name,
    email,
    mobile,
    password,
    license_no,
    license_cert_path,
    degree_cert_path,
    id_proof_path,
    verification_status,
    created_at,
    updated_at
)
VALUES (?,?,?,?,?,?,?,?,?,NOW(),NOW())
");

$stmt->bind_param(
    "sssssssss",
    $fullName,
    $email,
    $mobile,
    $hashedPassword,
    $licenseNo,
    $licensePath,
    $degreePath,
    $idPath,
    $verificationStatus
);

if ($stmt->execute()) {
    $stmt->close();
    $conn->close();
    apiResponse(true, "Registration successful. Waiting for admin approval.", null, 201);
}

/* ---------- FAILURE ---------- */
$stmt->close();
$conn->close();
apiResponse(false, "Registration failed", null, 500);
