<?php
/**
 * Get All Approved Doctors API
 * GET: get_all_doctors.php?status=Online (optional filter by doctor_mode)
 * Response: { "success": true, "data": [...] }
 */

header("Content-Type: application/json; charset=UTF-8");
require_once "config.php";

/* ---------- ONLY GET ---------- */
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    sendResponse(false, "Only GET method allowed", null, 405);
}

/* ---------- GET OPTIONAL FILTER ---------- */
$statusFilter = isset($_GET['status']) ? trim($_GET['status']) : '';

/* ---------- DB CONNECTION ---------- */
$conn = getDBConnectionSafe();
if (!$conn) {
    sendResponse(false, "Database connection failed", null, 500);
}

/* ---------- BUILD QUERY ---------- */
$query = "
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
";

$params = [];
$types = "";

if (!empty($statusFilter)) {
    $query .= " AND doctor_mode = ?";
    $params[] = $statusFilter;
    $types .= "s";
}

$query .= " ORDER BY full_name ASC";

$stmt = $conn->prepare($query);

if (!empty($params)) {
    $stmt->bind_param($types, ...$params);
}

$stmt->execute();
$result = $stmt->get_result();

$doctors = [];
$doctorCount = 0;
while ($row = $result->fetch_assoc()) {
    $doctorCount++;
    
    // Calculate default rating (can be replaced with actual ratings later)
    $rating = 4.5 + (rand(0, 5) / 10); // Random between 4.5 and 5.0
    
    // Parse experience_years - handle both string and numeric values
    $experienceYears = 0;
    if (!empty($row['experience_years'])) {
        $expStr = trim($row['experience_years']);
        // Extract numeric value from string (e.g., "5 years" -> 5, "5" -> 5)
        if (preg_match('/(\d+)/', $expStr, $matches)) {
            $experienceYears = (int)$matches[1];
        } else {
            $experienceYears = (int)$expStr;
        }
    }
    
    // Parse consultation_fee - handle both decimal and integer values
    $consultationFee = 0;
    if (!empty($row['consultation_fee'])) {
        $feeValue = $row['consultation_fee'];
        if (is_numeric($feeValue)) {
            $consultationFee = (int)round((float)$feeValue);
        } else {
            $consultationFee = (int)round((float)$feeValue);
        }
    }
    
    // Get doctor name - prefer display_name, fallback to full_name
    $doctorName = '';
    if (!empty($row['display_name'])) {
        $doctorName = trim($row['display_name']);
    } elseif (!empty($row['full_name'])) {
        $doctorName = trim($row['full_name']);
    } else {
        $doctorName = 'Doctor';
    }
    
    // Get specialization - prefer primary_specialization
    $specialization = '';
    if (!empty($row['primary_specialization'])) {
        $specialization = trim($row['primary_specialization']);
    } else {
        $specialization = 'General Medicine';
    }
    
    // Get doctor mode/status
    $doctorStatus = 'Offline';
    if (!empty($row['doctor_mode'])) {
        $doctorStatus = trim($row['doctor_mode']);
        // Ensure valid status
        if (!in_array($doctorStatus, ['Online', 'Busy', 'Offline'])) {
            $doctorStatus = 'Offline';
        }
    }
    
    $doctors[] = [
        'doctor_id' => (int)$row['doctor_id'],
        'name' => $doctorName,
        'specialization' => $specialization,
        'experience' => $experienceYears,
        'fee' => $consultationFee,
        'rating' => round($rating, 1),
        'status' => $doctorStatus,
        'profile_image' => !empty($row['profile_image']) ? trim($row['profile_image']) : '',
        'clinic_name' => !empty($row['clinic_name']) ? trim($row['clinic_name']) : '',
        'clinic_address' => !empty($row['clinic_address']) ? trim($row['clinic_address']) : '',
        'sub_specialization' => !empty($row['sub_specialization']) ? trim($row['sub_specialization']) : ''
    ];
}

// Log for debugging
error_log("get_all_doctors.php: Found $doctorCount doctors with status='approved'");

$stmt->close();
$conn->close();

sendResponse(true, "Doctors retrieved successfully", $doctors, 200);
?>

