<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

require_once __DIR__ . "/email_config.php";

$result = sendOTPEmail(
    "yourgmail@gmail.com",   // 🔴 replace with REAL email
    rand(100000, 999999),
    "Test User"
);

echo $result ? "✅ Email sent" : "❌ Failed";
