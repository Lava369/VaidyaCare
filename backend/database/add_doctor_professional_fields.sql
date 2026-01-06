-- ============================================
-- Add Professional Details Fields to Doctors Table
-- Database: vaidyacare_db
-- ============================================

USE vaidyacare_db;

-- Add professional details columns to doctors table
ALTER TABLE `doctors` 
ADD COLUMN IF NOT EXISTS `doctor_mode` ENUM('Online', 'Offline', 'Busy') DEFAULT 'Offline' AFTER `status`,
ADD COLUMN IF NOT EXISTS `primary_specialization` VARCHAR(255) DEFAULT NULL AFTER `specialization`,
ADD COLUMN IF NOT EXISTS `sub_specialization` VARCHAR(255) DEFAULT NULL AFTER `primary_specialization`,
ADD COLUMN IF NOT EXISTS `medical_council` VARCHAR(255) DEFAULT NULL AFTER `license_no`,
ADD COLUMN IF NOT EXISTS `qualifications` VARCHAR(500) DEFAULT NULL AFTER `medical_council`,
ADD COLUMN IF NOT EXISTS `languages_spoken` VARCHAR(255) DEFAULT NULL AFTER `qualifications`,
ADD COLUMN IF NOT EXISTS `consultation_fee` DECIMAL(10,2) DEFAULT NULL AFTER `languages_spoken`;

-- Update existing specialization field to be primary_specialization if needed
-- (This is optional, only if you want to migrate existing data)
-- UPDATE doctors SET primary_specialization = specialization WHERE primary_specialization IS NULL AND specialization IS NOT NULL;

