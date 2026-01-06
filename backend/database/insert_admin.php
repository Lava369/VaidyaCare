<?php
/**
 * Script to create admin user
 * Run this file once to create an admin account
 * Usage: Access via browser: http://localhost/VaidyaCare/database/insert_admin.php
 */

require_once '../api/config.php';

// Only allow direct access (for setup purposes)
// In production, remove this file or add authentication

$conn = getDBConnection();

// Admin details
$email = 'admin@vaidyacare.com';
$password = 'admin123'; // Change this to your desired password
$name = 'Admin User';

// Hash the password
$hashedPassword = password_hash($password, PASSWORD_DEFAULT);

// Check if admin already exists
$checkStmt = $conn->prepare("SELECT admin_id FROM admins WHERE email = ?");
$checkStmt->bind_param("s", $email);
$checkStmt->execute();
$result = $checkStmt->get_result();

if ($result->num_rows > 0) {
    echo "<h2>Admin already exists!</h2>";
    echo "<p>Email: $email</p>";
    echo "<p>To update password, use the UPDATE query in the SQL file.</p>";
} else {
    // Insert admin
    $stmt = $conn->prepare("INSERT INTO admins (email, password, name, status) VALUES (?, ?, ?, 'active')");
    $stmt->bind_param("sss", $email, $hashedPassword, $name);
    
    if ($stmt->execute()) {
        echo "<h2>Admin created successfully!</h2>";
        echo "<p>Email: $email</p>";
        echo "<p>Password: $password</p>";
        echo "<p><strong>Please change the password after first login!</strong></p>";
    } else {
        echo "<h2>Error creating admin:</h2>";
        echo "<p>" . $stmt->error . "</p>";
    }
    $stmt->close();
}

$checkStmt->close();
$conn->close();
?>

