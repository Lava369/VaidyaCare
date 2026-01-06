# Patient Profile API Documentation

## Overview
Backend APIs for managing patient profile data from `EditProfile.kt`.

## Database Setup

### 1. Run Database Migration
Execute the SQL script to add profile fields to the `users` table:

```sql
-- File: database/add_patient_profile_fields.sql
-- This adds: dob, blood_group, height_cm, weight_kg, address, city, state, pin
```

**Run in phpMyAdmin:**
1. Select database: `vaidyacare_db`
2. Go to SQL tab
3. Copy and paste the contents of `add_patient_profile_fields.sql`
4. Click "Go"

### 2. Verify Table Structure
The `users` table should have these columns:
- `id` (PRIMARY KEY)
- `full_name`
- `email`
- `mobile`
- `age`
- `gender`
- `dob` (DATE) - **NEW**
- `blood_group` (VARCHAR) - **NEW**
- `height_cm` (VARCHAR) - **NEW**
- `weight_kg` (VARCHAR) - **NEW**
- `address` (VARCHAR) - **NEW**
- `city` (VARCHAR) - **NEW**
- `state` (VARCHAR) - **NEW**
- `pin` (VARCHAR) - **NEW**
- `password`
- `patient_id`
- `created_at`
- `updated_at`

## API Endpoints

### 1. Get Patient Profile
**GET** `patientget_profile.php`

**Parameters:**
- `email` (optional): Patient email address
- `user_id` (optional): Patient user ID

**Example:**
```
GET http://10.26.77.190/vaidyacare/api/patientget_profile.php?email=user@example.com
```

**Response:**
```json
{
  "success": true,
  "message": "Profile retrieved successfully",
  "data": {
    "full_name": "John Doe",
    "email": "user@example.com",
    "mobile": "9876543210",
    "age": "25",
    "gender": "Male",
    "dob": "1990-05-15",
    "blood_group": "O+",
    "height_cm": "175",
    "weight_kg": "70",
    "address": "123 Main Street",
    "city": "Mumbai",
    "state": "Maharashtra",
    "pin": "400001",
    "patient_id": "VD00001"
  }
}
```

### 2. Save Patient Profile
**POST** `patientSave_profile.php`

**Request Body (JSON):**
```json
{
  "full_name": "John Doe",
  "email": "user@example.com",
  "mobile": "9876543210",
  "dob": "1990-05-15",
  "gender": "Male",
  "blood_group": "O+",
  "height": "175",
  "weight": "70",
  "address": "123 Main Street",
  "city": "Mumbai",
  "state": "Maharashtra",
  "pin": "400001"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Profile updated successfully"
}
```

## Testing with Postman

1. Import `Postman_Collection_Patient_Profile.json` into Postman
2. Update the base URL if needed
3. Test each endpoint with sample data

## Data Flow

1. **Signup** (`SignupScreen.kt`) → Creates user in `users` table
2. **Edit Profile** (`EditProfile.kt`) → 
   - Loads data from `patientget_profile.php`
   - Saves data to `patientSave_profile.php`
3. **Profile Screens** → Display data from API

## Troubleshooting

### Issue: "Patient not found"
- **Solution**: Ensure the user exists in the `users` table (created via signup)
- Check: `SELECT * FROM users WHERE email = 'user@example.com';`

### Issue: "Database error"
- **Solution**: Run `add_patient_profile_fields.sql` to add missing columns
- Check: `DESCRIBE users;` to verify all columns exist

### Issue: Empty/null values
- **Solution**: Ensure data is saved via `EditProfile.kt` first
- Check: `SELECT dob, blood_group, height_cm FROM users WHERE email = 'user@example.com';`

## File Locations

- **Backend APIs:**
  - `api/patientget_profile.php` - Get profile
  - `api/patientSave_profile.php` - Save profile
  
- **Database:**
  - `database/add_patient_profile_fields.sql` - Migration script
  
- **Postman:**
  - `Postman_Collection_Patient_Profile.json` - API collection

