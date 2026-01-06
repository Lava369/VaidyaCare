-- VaidyaCare Database Schema
-- Database: vaidyacare_db

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS vaidyacare_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE vaidyacare_db;

-- Patients/Users Table
CREATE TABLE IF NOT EXISTS patients (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    mobile VARCHAR(20) NOT NULL UNIQUE,
    age INT NOT NULL,
    gender ENUM('Male', 'Female', 'Other') NOT NULL,
    password VARCHAR(255) NOT NULL,
    patient_id VARCHAR(20) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_mobile (mobile),
    INDEX idx_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data (optional - for testing)
-- Password: password123 (hashed)
-- INSERT INTO patients (full_name, email, mobile, age, gender, password, patient_id) 
-- VALUES ('Test User', 'test@example.com', '+919876543210', 25, 'Male', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'PAT000001');

