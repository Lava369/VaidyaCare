<?php
/**
 * Get User Profile API Endpoint
 * GET /api/get_profile.php?user_id=1
 * 
 * Response (JSON):
 * {
 *   "success": true,
 *   "message": "Profile retrieved successfully",
 *   "data": {
 *     "id": 1,
 *     "full_name": "John Doe",
 *     "email": "john@example.com",
 *     "mobile": "+919876543210",
 *     "age": 25,
 *     "gender": "Male",
 *     "patient_id": "PAT000001",
 *     "created_at": "2024-01-01 12:00:00"
 *   }
 * }
 */

// Include config - handle errors gracefully
try {
    require_once 'config.php';
} catch (Exception $e) {
    header('Content-Type: application/json');
    echo json_encode(['success' => false, 'message' => 'Server configuration error'], JSON_UNESCAPED_UNICODE);
    exit();
}

// Only allow GET requests
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    sendResponse(false, 'Only GET method is allowed', null, 405);
}

// Get user_id from query parameter
if (!isset($_GET['user_id']) || empty(trim($_GET['user_id']))) {
    sendResponse(false, 'user_id parameter is required', null, 400);
}

$userId = intval($_GET['user_id']);

if ($userId <= 0 || !is_numeric($_GET['user_id'])) {
    sendResponse(false, 'Invalid user_id. Must be a positive integer', null, 400);
}

// Get database connection
$conn = getDBConnection();
if ($conn === null) {
    sendResponse(false, 'Database connection failed', null, 500);
}

// Query user profile
$stmt = $conn->prepare("SELECT user_id, full_name, email, mobile, age, gender, patient_id, created_at, updated_at FROM patients WHERE user_id = ? LIMIT 1");
$stmt->bind_param("i", $userId);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    $stmt->close();
    $conn->close();
    sendResponse(false, 'User not found', null, 404);
}

$user = $result->fetch_assoc();
$stmt->close();
$conn->close();

// Validate user data exists
if (!$user || empty($user['user_id'])) {
    sendResponse(false, 'Invalid user data', null, 500);
}

// Prepare response data
$responseData = [
    'id' => intval($user['user_id']),
    'full_name' => $user['full_name'],
    'email' => $user['email'],
    'mobile' => $user['mobile'],
    'age' => intval($user['age']),
    'gender' => $user['gender'],
    'patient_id' => $user['patient_id'],
    'created_at' => $user['created_at']
];

sendResponse(true, 'Profile retrieved successfully', $responseData, 200);

