<?php
/**
 * Test script for signup endpoint
 * This helps debug JSON response issues
 */

// Test the signup endpoint
$testData = [
    'full_name' => 'Test User',
    'email' => 'test@example.com',
    'mobile' => '+919876543210',
    'age' => 25,
    'gender' => 'Male',
    'password' => 'test123'
];

$jsonData = json_encode($testData);

$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, 'http://localhost/VaidyaCare/api/signup.php');
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, $jsonData);
curl_setopt($ch, CURLOPT_HTTPHEADER, [
    'Content-Type: application/json',
    'Content-Length: ' . strlen($jsonData)
]);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

$response = curl_exec($ch);
$httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

echo "HTTP Code: $httpCode\n";
echo "Response:\n";
echo $response . "\n\n";

// Validate JSON
$decoded = json_decode($response, true);
if (json_last_error() === JSON_ERROR_NONE) {
    echo "✓ Valid JSON\n";
    print_r($decoded);
} else {
    echo "✗ Invalid JSON: " . json_last_error_msg() . "\n";
    echo "Raw response (first 500 chars):\n";
    echo substr($response, 0, 500) . "\n";
}
?>

