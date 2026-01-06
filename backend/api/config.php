<?php
/**
 * VaidyaCare - Global Config & Helpers
 */

/* ---------- ERROR HANDLING ---------- */
/* 🔴 SET TO 0 IN PRODUCTION */
error_reporting(E_ALL);
ini_set('display_errors', 1);

/* ---------- DATABASE CONFIG ---------- */
define('DB_HOST', '127.0.0.1');
define('DB_USER', 'root');
define('DB_PASS', '');
define('DB_NAME', 'vaidyacare_db');

/* ---------- ORIGINAL DB CONNECTION (UNCHANGED) ---------- */
function getDBConnection() {
    $conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);

    if ($conn->connect_error) {
        // ❌ This throws and causes HTTP 500
        throw new Exception("Database connection failed");
    }

    $conn->set_charset("utf8mb4");
    return $conn;
}

/* ---------- ✅ SAFE DB CONNECTION (ADDED) ---------- */
/* Use THIS in APIs to avoid HTTP 500 */
function getDBConnectionSafe() {
    $conn = @new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);

    if ($conn->connect_error) {
        return false; // ✅ NO exception
    }

    $conn->set_charset("utf8mb4");
    return $conn;
}

/* ---------- RESPONSE HELPER ---------- */
function sendResponse($success, $message, $data = null, $statusCode = 200) {
    // Aggressively clean all output buffers
    while (ob_get_level() > 0) {
        @ob_end_clean();
    }
    @ob_clean();
    
    // Suppress any errors that might output HTML
    @error_reporting(0);
    @ini_set('display_errors', 0);
    
    http_response_code($statusCode);
    header("Content-Type: application/json; charset=UTF-8");
    header("X-Content-Type-Options: nosniff");

    $response = [
        "success" => $success,
        "message" => $message
    ];

    if ($data !== null) {
        $response["data"] = $data;
    }

    echo json_encode($response, JSON_UNESCAPED_UNICODE);
    exit;
}

/* ---------- VALIDATION HELPERS ---------- */
function isValidEmail($email) {
    return filter_var($email, FILTER_VALIDATE_EMAIL);
}

function normalizeMobile($mobile) {
    return preg_replace('/\s+/', '', $mobile);
}

function isValidMobile($mobile) {
    return preg_match('/^\+\d{10,15}$/', $mobile);
}

function generatePatientId() {
    return "PAT" . str_pad(rand(1, 999999), 6, "0", STR_PAD_LEFT);
}
