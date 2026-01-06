-- ============================================
-- Admin Table Creation Script
-- Database: vaidyacare_db
-- ============================================

USE vaidyacare_db;

-- Create admins table if it doesn't exist
CREATE TABLE IF NOT EXISTS `admins` (
  `admin_id` INT(11) NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(255) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `status` ENUM('active', 'inactive') DEFAULT 'active',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`admin_id`),
  INDEX `idx_email` (`email`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert default admin account
-- Password: admin123 (hashed using password_hash PHP function)
-- You can generate a new hash using: echo password_hash('your_password', PASSWORD_DEFAULT);
INSERT INTO `admins` (`email`, `password`, `name`, `status`) 
VALUES 
('admin@vaidyacare.com', '$2y$10$iinkiAE8xmWKkbHWUQGK9.jY6NO/g1mf.4HOrvu8DTkjOx2Uvl5Z.', 'Admin User', 'active')
ON DUPLICATE KEY UPDATE `email` = `email`;

-- Note: The default password hash above is for 'admin123'
-- To create a new admin with a custom password, use PHP:
-- $hashedPassword = password_hash('your_password', PASSWORD_DEFAULT);
-- Then insert into database

