-- ============================================
-- Create Family Members Table
-- Database: vaidyacare_db
-- ============================================

USE vaidyacare_db;

-- Create family_members table
CREATE TABLE IF NOT EXISTS `family_members` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `user_id` INT(11) NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `relation` VARCHAR(50) NOT NULL,
    `age` INT(3) NOT NULL,
    `gender` VARCHAR(20) DEFAULT NULL,
    `phone` VARCHAR(20) DEFAULT NULL,
    `blood_group` VARCHAR(10) DEFAULT NULL,
    `address` TEXT DEFAULT NULL,
    `medical_history` TEXT DEFAULT NULL,
    `allergies` TEXT DEFAULT NULL,
    `medications` TEXT DEFAULT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_family_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

