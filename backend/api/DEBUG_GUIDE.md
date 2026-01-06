# Debug Guide for JSON Parsing Error

## Error Message
```
Expected BEGIN_OBJECT but was STRING at line 2 column 1 path $
```

This means the server is returning plain text/string instead of JSON.

## Quick Checks

### 1. Test API Endpoint Directly

Open in browser:
```
http://localhost/VaidyaCare/api/test_api.php
```

OR

```
http://10.26.77.190/vaidyacare/api/test_api.php
```

**Expected:** JSON response
**If you see:** HTML/text → PHP error or server issue

### 2. Check Apache Error Log

Location: `C:\xampp\apache\logs\error.log`

Look for:
- PHP fatal errors
- Missing functions
- Database connection errors

### 3. Check PHP Error Log

Location: `C:\xampp\php\logs\php_error_log`

### 4. Verify Database Connection

1. Open phpMyAdmin: http://localhost/phpmyadmin
2. Check if database `vaidyacare_db` exists
3. Check if table `patients` exists
4. Test connection manually

### 5. Test Signup Endpoint with cURL

**Windows PowerShell:**
```powershell
$body = @{
    full_name = "Test User"
    email = "test@example.com"
    mobile = "+919876543210"
    age = 25
    gender = "Male"
    password = "test123"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost/VaidyaCare/api/signup.php" `
    -Method POST `
    -Body $body `
    -ContentType "application/json" | Select-Object -ExpandProperty Content
```

**Expected:** JSON response
**If you see:** HTML/text → PHP error

## Common Issues & Solutions

### Issue 1: Database Connection Failed
**Symptom:** API returns error message as text
**Solution:**
- Check MySQL service is running
- Verify database credentials in config.php
- Ensure database exists

### Issue 2: PHP Fatal Error
**Symptom:** HTML error page or blank page
**Solution:**
- Check error logs
- Fix PHP syntax errors
- Ensure all functions are defined

### Issue 3: Output Before Headers
**Symptom:** Warning messages in response
**Solution:**
- Already fixed in config.php (output buffering)
- Removed closing PHP tags
- Error display suppressed

### Issue 4: Missing Functions
**Symptom:** Fatal error about undefined function
**Solution:**
- Ensure config.php is included
- Check function names match

### Issue 5: Network/Connection Issue
**Symptom:** Cannot connect to server
**Solution:**
- Check Apache is running
- Verify IP address is correct
- Check firewall settings
- Test from browser first

## Step-by-Step Debugging

1. ✅ Test `test_api.php` - Should return JSON
2. ✅ Check error logs - Look for PHP errors
3. ✅ Verify database - Exists and accessible
4. ✅ Test signup endpoint - Use cURL/Postman
5. ✅ Check Android app - Verify URL is correct
6. ✅ Test from browser - Direct API call

## Files to Check

- ✅ `config.php` - Database connection
- ✅ `signup.php` - Signup endpoint
- ✅ Error logs - Apache and PHP
- ✅ Database - phpMyAdmin

## Still Not Working?

1. **Enable Error Display Temporarily:**
   In config.php, change:
   ```php
   ini_set('display_errors', 1);  // Temporarily
   ```

2. **Check Raw Response:**
   Use browser developer tools or Postman to see exact response

3. **Test Each Component:**
   - Test config.php alone
   - Test database connection
   - Test JSON encoding
   - Test API endpoint

---

**Last Updated:** All fixes applied to prevent JSON parsing errors



