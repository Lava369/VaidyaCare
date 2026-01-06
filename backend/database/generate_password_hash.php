<?php
/**
 * Password Hash Generator
 * Use this to generate password hashes for admin accounts
 * Access via browser: http://localhost/VaidyaCare/database/generate_password_hash.php?password=your_password
 */

$password = $_GET['password'] ?? 'admin123';

if (isset($_GET['password'])) {
    $hashed = password_hash($password, PASSWORD_DEFAULT);
    
    echo "<h2>Password Hash Generator</h2>";
    echo "<p><strong>Password:</strong> " . htmlspecialchars($password) . "</p>";
    echo "<p><strong>Hash:</strong> <code>" . htmlspecialchars($hashed) . "</code></p>";
    echo "<hr>";
    echo "<p>Use this hash in your SQL INSERT statement:</p>";
    echo "<pre>INSERT INTO admins (email, password, name, status) VALUES ('admin@vaidyacare.com', '" . htmlspecialchars($hashed) . "', 'Admin User', 'active');</pre>";
} else {
    echo "<h2>Password Hash Generator</h2>";
    echo "<p>Usage: Add ?password=your_password to the URL</p>";
    echo "<p>Example: <a href='?password=admin123'>?password=admin123</a></p>";
    echo "<hr>";
    echo "<p>Default password 'admin123' hash:</p>";
    $hashed = password_hash($password, PASSWORD_DEFAULT);
    echo "<pre>" . htmlspecialchars($hashed) . "</pre>";
}
?>

