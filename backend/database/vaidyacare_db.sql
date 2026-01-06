-- VaidyaCare Database Schema
-- Run this SQL in phpMyAdmin to create the database and tables

CREATE DATABASE IF NOT EXISTS vaidyacare_db;
USE vaidyacare_db;

-- Users table for patient signup and login
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    mobile VARCHAR(20) UNIQUE NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(10) NOT NULL,
    password VARCHAR(255) NOT NULL,
    patient_id VARCHAR(20) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_mobile (mobile),
    INDEX idx_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Function to generate patient ID (VD + random number)
DELIMITER //
CREATE FUNCTION IF NOT EXISTS generate_patient_id() RETURNS VARCHAR(20)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE new_id VARCHAR(20);
    DECLARE id_exists INT;
    
    REPEAT
        SET new_id = CONCAT('VD', LPAD(FLOOR(RAND() * 99999), 5, '0'));
        SELECT COUNT(*) INTO id_exists FROM users WHERE patient_id = new_id;
    UNTIL id_exists = 0 END REPEAT;
    
    RETURN new_id;
END//
DELIMITER ;

