-- ============================================
-- Doctors Table Creation Script
-- Database: vaidyacare_db
-- ============================================

USE vaidyacare_db;

-- Create doctors table if it doesn't exist
CREATE TABLE IF NOT EXISTS `doctors` (
  `doctor_id` INT(11) NOT NULL AUTO_INCREMENT,
  `full_name` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL UNIQUE,
  `mobile` VARCHAR(20) NOT NULL UNIQUE,
  `license_no` VARCHAR(100) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `license_cert_path` VARCHAR(500) DEFAULT NULL,
  `degree_cert_path` VARCHAR(500) DEFAULT NULL,
  `id_proof_path` VARCHAR(500) DEFAULT NULL,
  `status` ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
  `specialization` VARCHAR(255) DEFAULT NULL,
  `experience_years` VARCHAR(10) DEFAULT NULL,
  `clinic_name` VARCHAR(255) DEFAULT NULL,
  `registration_number` VARCHAR(100) DEFAULT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `approved_at` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`doctor_id`),
  INDEX `idx_email` (`email`),
  INDEX `idx_mobile` (`mobile`),
  INDEX `idx_license_no` (`license_no`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Note: 
-- - status: 'pending' = waiting for admin approval
-- - status: 'approved' = can login
-- - status: 'rejected' = cannot login
-- - When admin approves, status changes to 'approved' and approved_at is set

