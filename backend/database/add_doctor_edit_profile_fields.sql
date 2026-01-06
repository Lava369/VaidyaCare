-- ============================================
-- Add Edit Profile Fields to Doctors Table
-- Database: vaidyacare_db
-- ============================================

USE vaidyacare_db;

-- Add edit profile columns to doctors table
-- Note: MySQL doesn't support IF NOT EXISTS in ALTER TABLE, so run this script only once
-- If columns already exist, you'll get an error - that's okay, just skip those lines

ALTER TABLE `doctors` 
ADD COLUMN `display_name` VARCHAR(255) DEFAULT NULL AFTER `full_name`;

ALTER TABLE `doctors` 
ADD COLUMN `bio` TEXT DEFAULT NULL AFTER `display_name`;

ALTER TABLE `doctors` 
ADD COLUMN `clinic_address` VARCHAR(500) DEFAULT NULL AFTER `clinic_name`;

-- Update display_name from full_name if display_name is NULL
UPDATE doctors SET display_name = full_name WHERE display_name IS NULL;

