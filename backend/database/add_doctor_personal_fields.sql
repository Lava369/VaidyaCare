-- ============================================
-- Add Personal Information Fields to Doctors Table
-- Database: vaidyacare_db
-- ============================================

USE vaidyacare_db;

-- Add personal information columns to doctors table
ALTER TABLE `doctors` 
ADD COLUMN IF NOT EXISTS `location` VARCHAR(255) DEFAULT NULL AFTER `mobile`,
ADD COLUMN IF NOT EXISTS `birth_month` VARCHAR(50) DEFAULT 'January' AFTER `location`,
ADD COLUMN IF NOT EXISTS `gender` ENUM('Male', 'Female', 'Other') DEFAULT 'Male' AFTER `birth_month`,
ADD COLUMN IF NOT EXISTS `profile_image` VARCHAR(500) DEFAULT NULL AFTER `gender`;

