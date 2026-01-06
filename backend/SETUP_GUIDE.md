# VaidyaCare Backend Setup Guide

## Quick Setup Steps

### Step 1: Database Setup

1. **Start XAMPP Services:**
   - Open XAMPP Control Panel
   - Start **Apache** service
   - Start **MySQL** service

2. **Create Database:**
   - Open phpMyAdmin: http://localhost/phpmyadmin
   - Click on "New" in the left sidebar
   - Database name: `vaidyacare_db`
   - Collation: `utf8mb4_unicode_ci`
   - Click "Create"

3. **Import Schema:**
   - Select `vaidyacare_db` database
   - Click "Import" tab
   - Click "Choose File" and select: `C:\xampp\htdocs\VaidyaCare\api\database_schema.sql`
   - Click "Go" at the bottom

   **OR** copy and paste the SQL from `database_schema.sql` into the SQL tab and execute.

### Step 2: Verify File Structure

Your directory should look like this:
```
C:\xampp\htdocs\VaidyaCare\
├── api\
│   ├── config.php
│   ├── signup.php
│   ├── Login.php
│   ├── get_profile.php
│   ├── database_schema.sql
│   ├── README.md
│   └── VaidyaCare_API.postman_collection.json
└── SETUP_GUIDE.md
```

### Step 3: Test the API

#### Option A: Using Browser (for GET requests)
- Open: http://localhost/VaidyaCare/api/get_profile.php?user_id=1
- Should show JSON error (user not found) - this confirms API is working

#### Option B: Using Postman
1. Open Postman
2. Click "Import" → "File"
3. Select: `C:\xampp\htdocs\VaidyaCare\api\VaidyaCare_API.postman_collection.json`
4. Test the "User Signup" request first
5. Use the returned `user_id` to test "Get User Profile"

### Step 4: Update Android App Base URL (if needed)

If you're testing locally, you may need to update the BASE_URL in your Android app's RetrofitClient:
- Local testing: `http://10.0.2.2/VaidyaCare/api/` (Android Emulator)
- Network testing: `http://10.26.77.190/VaidyaCare/api/` (Your network IP)

**Note:** Make sure the directory name in the URL matches your actual directory name. If your directory is `VaidyaCare` but URL uses `vaidyacare`, it should still work on Windows (case-insensitive), but for consistency, use the correct case.

### Step 5: Test Integration

1. **Test Signup from App:**
   - Open your Android app
   - Navigate to Signup Screen
   - Fill in the form and submit
   - Check if account is created successfully

2. **Test Login from App:**
   - Use the credentials you just created
   - Try logging in with email
   - Try logging in with mobile number

3. **Test Profile from App:**
   - After login, navigate to Profile Screen
   - Verify user data is displayed correctly

## Common Issues & Solutions

### Issue 1: "Database connection failed"
**Solution:**
- Ensure MySQL service is running in XAMPP
- Check database name is exactly `vaidyacare_db`
- Verify database exists in phpMyAdmin

### Issue 2: "Access denied for user 'root'"
**Solution:**
- Check MySQL password in `config.php`
- Default XAMPP MySQL user is `root` with no password
- If you set a password, update `DB_PASS` in `config.php`

### Issue 3: API returns 404 or blank page
**Solution:**
- Ensure Apache service is running
- Check file paths are correct
- Verify files are in `C:\xampp\htdocs\VaidyaCare\api\`
- Try accessing: http://localhost/VaidyaCare/api/config.php (should show JSON error about method)

### Issue 4: CORS errors in browser/Postman
**Solution:**
- CORS headers are already set in `config.php`
- If issues persist, check Apache error logs
- Ensure PHP is enabled in XAMPP

### Issue 5: "Table 'patients' doesn't exist"
**Solution:**
- Database schema not imported
- Go to phpMyAdmin → Select `vaidyacare_db` → Import `database_schema.sql`
- Or manually run the SQL from `database_schema.sql`

### Issue 6: Signup works but Login fails
**Solution:**
- Check if password is being hashed correctly
- Verify email/mobile format matches what was used in signup
- Check database to see if user was created: `SELECT * FROM patients;`

## Database Verification

Run this SQL in phpMyAdmin to verify setup:
```sql
USE vaidyacare_db;
SHOW TABLES;
DESCRIBE patients;
SELECT * FROM patients;
```

## Testing Checklist

- [ ] XAMPP Apache service running
- [ ] XAMPP MySQL service running
- [ ] Database `vaidyacare_db` created
- [ ] Table `patients` exists with correct structure
- [ ] Can access API endpoints via browser/Postman
- [ ] Signup endpoint creates user successfully
- [ ] Login endpoint works with created credentials
- [ ] Get Profile endpoint returns user data
- [ ] Android app can connect to API
- [ ] Signup from app works
- [ ] Login from app works
- [ ] Profile screen loads data

## Next Steps

Once everything is working:
1. Consider adding more API endpoints as needed
2. Implement additional validation
3. Add API rate limiting
4. Set up proper error logging
5. Consider using environment variables for database credentials
6. Implement JWT tokens for authentication (optional)

## Support

For issues:
1. Check PHP error logs: `C:\xampp\php\logs\php_error_log`
2. Check Apache error logs: `C:\xampp\apache\logs\error.log`
3. Check MySQL error logs: `C:\xampp\mysql\data\*.err`
4. Verify all file permissions
5. Ensure PHP version is 7.4 or higher

