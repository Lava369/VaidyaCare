-- ============================================
-- Add Profile Fields to Users Table
-- Database: vaidyacare_db
-- ============================================

USE vaidyacare_db;

-- Add profile fields to users table (if they don't exist)
ALTER TABLE `users` 
ADD COLUMN IF NOT EXISTS `dob` DATE DEFAULT NULL AFTER `gender`,
ADD COLUMN IF NOT EXISTS `blood_group` VARCHAR(10) DEFAULT NULL AFTER `dob`,
ADD COLUMN IF NOT EXISTS `height_cm` VARCHAR(10) DEFAULT NULL AFTER `blood_group`,
ADD COLUMN IF NOT EXISTS `weight_kg` VARCHAR(10) DEFAULT NULL AFTER `height_cm`,
ADD COLUMN IF NOT EXISTS `address` VARCHAR(500) DEFAULT NULL AFTER `weight_kg`,
ADD COLUMN IF NOT EXISTS `city` VARCHAR(100) DEFAULT NULL AFTER `address`,
ADD COLUMN IF NOT EXISTS `state` VARCHAR(100) DEFAULT NULL AFTER `city`,
ADD COLUMN IF NOT EXISTS `pin` VARCHAR(10) DEFAULT NULL AFTER `state`;

-- If patients table exists, we can also update it
-- But for now, we'll use users table for consistency

