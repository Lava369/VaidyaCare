<?php
// Test script - DELETE after testing for security
// Access: http://localhost/VaidyaCare/api/test_admin.php

require_once 'config.php';

$email = 'admin@vaidyacare.com';
$password = 'admin123';

// Generate correct hash
$correctHash = password_hash($password, PASSWORD_DEFAULT);

echo "<h2>Admin Login Test</h2>";
echo "<p><strong>Email:</strong> $email</p>";
echo "<p><strong>Password:</strong> $password</p>";
echo "<p><strong>Generated Hash:</strong> $correctHash</p>";
echo "<hr>";

// Connect to database
$conn = getDBConnection();

// Check if admin exists
$stmt = $conn->prepare("SELECT id, email, password, full_name FROM admins WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo "<p style='color: red;'><strong>ERROR:</strong> Admin not found in database!</p>";
    echo "<p>Run this SQL to create admin:</p>";
    echo "<pre>INSERT INTO admins (email, password, full_name) VALUES ('$email', '$correctHash', 'System Administrator');</pre>";
} else {
    $admin = $result->fetch_assoc();
    echo "<p><strong>Admin Found:</strong> " . $admin['full_name'] . "</p>";
    echo "<p><strong>Stored Hash:</strong> " . $admin['password'] . "</p>";
    
    // Test password verification
    $isValid = password_verify($password, $admin['password']);
    
    if ($isValid) {
        echo "<p style='color: green;'><strong>✓ Password verification: SUCCESS</strong></p>";
    } else {
        echo "<p style='color: red;'><strong>✗ Password verification: FAILED</strong></p>";
        echo "<p>Update the password hash in database:</p>";
        echo "<pre>UPDATE admins SET password = '$correctHash' WHERE email = '$email';</pre>";
    }
}

$stmt->close();
$conn->close();
?>