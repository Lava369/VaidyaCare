<?php
/**
 * API Test File
 * Access this file to verify your API is working:
 * http://10.26.77.190/vaidyacare/api/test.php
 * or
 * http://localhost/VaidyaCare/api/test.php
 */

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

$response = [
    'success' => true,
    'message' => 'VaidyaCare API is working!',
    'server_info' => [
        'php_version' => phpversion(),
        'server_software' => $_SERVER['SERVER_SOFTWARE'] ?? 'Unknown',
        'request_method' => $_SERVER['REQUEST_METHOD'],
        'request_uri' => $_SERVER['REQUEST_URI'] ?? 'Unknown',
        'document_root' => $_SERVER['DOCUMENT_ROOT'] ?? 'Unknown'
    ],
    'api_endpoints' => [
        'signup' => '/api/signup.php',
        'login' => '/api/Login.php',
        'get_profile' => '/api/get_profile.php'
    ],
    'database_status' => 'Check config.php for database connection'
];

echo json_encode($response, JSON_PRETTY_PRINT);
?>

