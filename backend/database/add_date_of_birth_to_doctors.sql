-- ============================================
-- Add Date of Birth to Doctors Table
-- Database: vaidyacare_db
-- ============================================

USE vaidyacare_db;

-- Add date_of_birth column to doctors table
ALTER TABLE `doctors` 
ADD COLUMN `date_of_birth` DATE DEFAULT NULL AFTER `full_name`;

