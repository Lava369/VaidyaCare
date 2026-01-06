# API URL Verification Guide

## Your API Base URL
```
http://10.26.77.190/vaidyacare/api/
```

## File Location
```
C:\xampp\htdocs\VaidyaCare\api\
```

## Important Note About Case Sensitivity

Your Android app uses: `/vaidyacare/api/` (lowercase)  
Your folder is named: `/VaidyaCare/api/` (capital letters)

**On Windows:** This works fine because Windows file system is case-insensitive. Apache will serve both URLs correctly.

**However, for consistency**, you have two options:

### Option 1: Keep Current Setup (Recommended)
- Keep folder name as `VaidyaCare` (with capitals)
- URLs `/vaidyacare/` and `/VaidyaCare/` both work on Windows
- No changes needed

### Option 2: Rename Folder (Optional)
- Rename folder to match URL exactly: `C:\xampp\htdocs\vaidyacare\`
- This ensures consistency across all platforms
- Requires moving files and updating paths

## Test Your API

### 1. Test API Connection
Open in browser:
```
http://10.26.77.190/vaidyacare/api/test.php
```
or
```
http://localhost/VaidyaCare/api/test.php
```

You should see a JSON response confirming the API is working.

### 2. Test Endpoints

#### Signup Endpoint
```
POST http://10.26.77.190/vaidyacare/api/signup.php
```

#### Login Endpoint
```
POST http://10.26.77.190/vaidyacare/api/Login.php
```

#### Get Profile Endpoint
```
GET http://10.26.77.190/vaidyacare/api/get_profile.php?user_id=1
```

## Network Access

To access from your Android device on the same network:
1. Ensure your PC and Android device are on the same Wi-Fi network
2. Use your PC's IP address: `10.26.77.190`
3. Make sure Windows Firewall allows Apache (port 80)

### Check Apache is Accessible
1. Open Command Prompt
2. Run: `netstat -an | findstr :80`
3. You should see Apache listening on port 80

### Firewall Settings
If API is not accessible:
1. Open Windows Defender Firewall
2. Allow Apache HTTP Server through firewall
3. Or allow port 80 for inbound connections

## Android App Configuration

Your Android app is already configured with:
```kotlin
BASE_URL = "http://10.26.77.190/vaidyacare/api/"
```

This matches your setup perfectly!

## Quick Verification Checklist

- [ ] XAMPP Apache service is running
- [ ] XAMPP MySQL service is running  
- [ ] Can access: http://localhost/VaidyaCare/api/test.php
- [ ] Can access: http://10.26.77.190/vaidyacare/api/test.php (from same PC)
- [ ] Can access from Android device on same network
- [ ] Database `vaidyacare_db` is created
- [ ] Table `patients` exists
- [ ] All PHP files are in `C:\xampp\htdocs\VaidyaCare\api\`

## Troubleshooting Network Access

### Cannot access from Android device:

1. **Check IP Address:**
   ```cmd
   ipconfig
   ```
   Look for IPv4 Address (should be 10.26.77.190)

2. **Ping Test:**
   From Android device, try pinging: `10.26.77.190`

3. **Check Apache Virtual Hosts:**
   If using virtual hosts, ensure `10.26.77.190` is configured

4. **Use Localhost for Testing:**
   For emulator testing, use: `http://10.0.2.2/VaidyaCare/api/`
   (10.0.2.2 is special IP for Android emulator to access host machine)

## All API Files

✅ `config.php` - Database configuration  
✅ `signup.php` - User signup endpoint  
✅ `Login.php` - User login endpoint  
✅ `get_profile.php` - Get user profile endpoint  
✅ `test.php` - API test/verification file  
✅ `database_schema.sql` - Database schema  
✅ `import_to_phpmyadmin.sql` - Simplified import file  

Your backend is ready! 🚀

