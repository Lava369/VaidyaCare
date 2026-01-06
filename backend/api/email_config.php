<?php
/**
 * Email Configuration File
 * 
 * This file handles email sending for OTP and other notifications.
 * Supports both PHP mail() and SMTP (via PHPMailer).
 */

// Email Configuration
// Set to false to use SMTP (requires PHPMailer)
// Use setup_email.php to configure this easily
define('USE_PHP_MAIL', false);  // Change to false after setting up Gmail SMTP

// PHPMailer namespace imports (must be at top level)
// Only used if PHPMailer is installed and USE_PHP_MAIL is false

// Email Settings (used for both PHP mail() and SMTP)
define('SMTP_FROM_EMAIL', 'noreply@helphup.com'); // Will be updated when you configure SMTP
define('SMTP_FROM_NAME', 'HelpHup');

// SMTP Configuration (only used if USE_PHP_MAIL = false)
// Configure these using setup_email.php or edit manually:
define('SMTP_HOST', 'smtp.gmail.com');
define('SMTP_PORT', 587);
define('SMTP_USERNAME', 'klavakumar39@gmail.com');  // Replace with your Gmail address
define('SMTP_PASSWORD', 'ixbz ktuh lwsl faua');     // Replace with Gmail 16-character App Password

/**
 * Send OTP Email
 * 
 * @param string $to Recipient email address
 * @param string $otp The OTP code to send
 * @param string $userType Type of user (NGO, Donor, Volunteer)
 * @return array ['success' => bool, 'message' => string]
 */
function sendOTPEmail($to, $otp, $userType = 'User') {
    // Validate email
    if (empty($to) || !filter_var($to, FILTER_VALIDATE_EMAIL)) {
        return ['success' => false, 'message' => 'Invalid email address'];
    }
    
    // Email subject
    $subject = "Your Password Reset OTP - HelpHup";
    
    // Email body
    $message = "
    <html>
    <head>
        <style>
            body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
            .container { max-width: 600px; margin: 0 auto; padding: 20px; }
            .header { background-color: #22C55E; color: white; padding: 20px; text-align: center; }
            .content { padding: 20px; background-color: #f9f9f9; }
            .otp-box { background-color: #22C55E; color: white; padding: 15px; text-align: center; font-size: 24px; font-weight: bold; margin: 20px 0; border-radius: 5px; }
            .footer { padding: 20px; text-align: center; color: #666; font-size: 12px; }
        </style>
    </head>
    <body>
        <div class='container'>
            <div class='header'>
                <h2>Password Reset Request</h2>
            </div>
            <div class='content'>
                <p>Hello,</p>
                <p>You have requested to reset your password for your HelpHup $userType account.</p>
                <p>Please use the following OTP code to reset your password:</p>
                <div class='otp-box'>$otp</div>
                <p>This OTP will expire in 15 minutes.</p>
                <p>If you did not request this password reset, please ignore this email.</p>
            </div>
            <div class='footer'>
                <p>This is an automated message from HelpHup. Please do not reply to this email.</p>
            </div>
        </div>
    </body>
    </html>
    ";
    
    // Email headers
    $headers = "MIME-Version: 1.0" . "\r\n";
    $headers .= "Content-type:text/html;charset=UTF-8" . "\r\n";
    $headers .= "From: " . SMTP_FROM_NAME . " <" . SMTP_FROM_EMAIL . ">" . "\r\n";
    $headers .= "Reply-To: " . SMTP_FROM_EMAIL . "\r\n";
    
    // Try to send email
    if (USE_PHP_MAIL) {
        // Use PHP mail() function
        $mailSent = @mail($to, $subject, $message, $headers);
        
        if ($mailSent) {
            return ['success' => true, 'message' => 'OTP sent successfully'];
        } else {
            // PHP mail() failed - this is normal on XAMPP without SMTP setup
            return [
                'success' => false, 
                'message' => 'Email sending failed - check email_config.php'
            ];
        }
    } else {
        // Use SMTP via PHPMailer
        return sendOTPEmailSMTP($to, $otp, $userType, $subject, $message);
    }
}

/**
 * Send OTP Email via SMTP (PHPMailer)
 * 
 * @param string $to Recipient email address
 * @param string $otp The OTP code
 * @param string $userType Type of user
 * @param string $subject Email subject
 * @param string $message Email body (HTML)
 * @return array ['success' => bool, 'message' => string]
 */
function sendOTPEmailSMTP($to, $otp, $userType, $subject, $message) {
    // Check if PHPMailer is available
    $phpmailerPath = __DIR__ . '/PHPMailer/src/Exception.php';
    if (!file_exists($phpmailerPath)) {
        return [
            'success' => false,
            'message' => 'PHPMailer not found. Please install PHPMailer library. OTP generated: ' . $otp
        ];
    }
    
    try {
        // Include PHPMailer files
        require_once __DIR__ . '/PHPMailer/src/Exception.php';
        require_once __DIR__ . '/PHPMailer/src/PHPMailer.php';
        require_once __DIR__ . '/PHPMailer/src/SMTP.php';
        
        // Use fully qualified class names instead of 'use' statements
        // (use statements must be at top level, not inside functions)
        $mail = new \PHPMailer\PHPMailer\PHPMailer(true);
        
        // Server settings
        $mail->isSMTP();
        $mail->Host = SMTP_HOST;
        $mail->SMTPAuth = true;
        $mail->Username = SMTP_USERNAME;
        $mail->Password = SMTP_PASSWORD;
        $mail->SMTPSecure = \PHPMailer\PHPMailer\PHPMailer::ENCRYPTION_STARTTLS;
        $mail->Port = SMTP_PORT;
        $mail->CharSet = 'UTF-8';
        
        // Recipients
        $mail->setFrom(SMTP_FROM_EMAIL, SMTP_FROM_NAME);
        $mail->addAddress($to);
        
        // Content
        $mail->isHTML(true);
        $mail->Subject = $subject;
        $mail->Body = $message;
        $mail->AltBody = strip_tags($message);
        
        $mail->send();
        return ['success' => true, 'message' => 'OTP sent successfully to your email'];
        
    } catch (\PHPMailer\PHPMailer\Exception $e) {
        return [
            'success' => false,
            'message' => 'Email sending failed: ' . (isset($mail) ? $mail->ErrorInfo : $e->getMessage()) . '. OTP generated: ' . $otp
        ];
    } catch (Exception $e) {
        return [
            'success' => false,
            'message' => 'Email sending failed: ' . $e->getMessage() . '. OTP generated: ' . $otp
        ];
    }
}
?>