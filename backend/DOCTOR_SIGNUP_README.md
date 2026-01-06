# Doctor Signup & Approval Flow

## Overview

The doctor registration system works with an approval workflow:
1. **Doctor Signup** → Creates account with `pending` status
2. **Admin Dashboard** → Shows pending doctors for approval
3. **Admin Approves** → Status changes to `approved`
4. **Doctor Login** → Only works if status is `approved`

## Setup Instructions

### 1. Database Setup

1. Open phpMyAdmin: `http://localhost/phpmyadmin`
2. Select database: `vaidyacare_db`
3. Run the SQL script: `database/create_doctors_table.sql`

### 2. Create Uploads Directory

Create the uploads directory for storing doctor documents:
```bash
mkdir C:\xampp\htdocs\VaidyaCare\uploads\doctors
```

Or create it via PHP by accessing: `http://localhost/VaidyaCare/api/doctor_signup.php` (it will auto-create)

### 3. File Permissions

Ensure the uploads directory is writable:
- Windows: Right-click folder → Properties → Security → Allow write permissions
- Or set permissions via XAMPP control panel

## API Endpoints

### 1. Doctor Signup
- **URL**: `http://localhost/VaidyaCare/api/doctor_signup.php`
- **Method**: `POST`
- **Content-Type**: `multipart/form-data`
- **Fields**:
  - `full_name` (text)
  - `email` (text)
  - `mobile` (text)
  - `license_no` (text)
  - `password` (text)
  - `license_cert` (file - PDF or image)
  - `degree_cert` (file - PDF or image)
  - `id_proof` (file - PDF or image)

**Success Response** (200):
```json
{
    "success": true,
    "message": "Registration successful! Your account is pending admin approval. You will be notified once approved.",
    "doctor_id": 1
}
```

### 2. Doctor Login
- **URL**: `http://localhost/VaidyaCare/api/doctor_login.php`
- **Method**: `POST`
- **Content-Type**: `application/json`
- **Body**:
```json
{
    "email": "doctor@example.com",
    "password": "password123"
}
```
OR
```json
{
    "mobile": "9876543210",
    "password": "password123"
}
```

**Success Response** (200):
```json
{
    "success": true,
    "message": "Login successful.",
    "doctor": {
        "id": 1,
        "full_name": "Dr. John Doe",
        "email": "doctor@example.com",
        "mobile": "9876543210",
        "license_no": "LIC123456"
    }
}
```

**Pending Approval Response** (403):
```json
{
    "success": false,
    "message": "Your account is pending approval. Please wait for admin approval."
}
```

### 3. Get Pending Doctors (Admin)
- **URL**: `http://localhost/VaidyaCare/api/get_pending_doctors.php`
- **Method**: `GET`
- **Response**:
```json
{
    "success": true,
    "data": [
        {
            "doctor_id": 1,
            "full_name": "Dr. John Doe",
            "specialization": "Not specified",
            "experience_years": "0",
            "clinic_name": "Not specified",
            "registration_number": "LIC123456"
        }
    ]
}
```

### 4. Approve Doctor (Admin)
- **URL**: `http://localhost/VaidyaCare/api/approve_doctor.php`
- **Method**: `POST`
- **Content-Type**: `application/x-www-form-urlencoded`
- **Body**: `doctor_id=1`
- **Response**:
```json
{
    "success": true,
    "message": "Doctor approved successfully."
}
```

### 5. Reject Doctor (Admin)
- **URL**: `http://localhost/VaidyaCare/api/reject_doctor.php`
- **Method**: `POST`
- **Content-Type**: `application/x-www-form-urlencoded`
- **Body**: `doctor_id=1`
- **Response**:
```json
{
    "success": true,
    "message": "Doctor rejected successfully."
}
```

## Workflow

1. **Doctor Registration**:
   - Doctor fills signup form in Android app
   - Uploads required documents (License, Degree, ID Proof)
   - Account created with `status = 'pending'`
   - Doctor receives message: "Registration successful! Your account is pending admin approval."

2. **Admin Approval**:
   - Admin logs into Admin Dashboard
   - Sees list of pending doctors
   - Reviews doctor information
   - Clicks "Approve" or "Reject"

3. **Doctor Login**:
   - If **approved**: Doctor can login successfully
   - If **pending**: Shows message "Your account is pending approval"
   - If **rejected**: Shows message "Your account has been rejected"

## File Storage

- Uploaded documents are stored in: `C:\xampp\htdocs\VaidyaCare\uploads\doctors/`
- File naming format: `{type}_{timestamp}_{uniqueid}.{extension}`
- Example: `license_1234567890_abc123.pdf`

## Security Notes

- Passwords are hashed using PHP's `password_hash()` function
- File uploads are validated (type, size max 5MB)
- Only approved doctors can login
- Email and mobile numbers must be unique

## Testing

1. Import `Postman_Collection_Doctor_API.json` into Postman
2. Test doctor signup with sample data
3. Check Admin Dashboard for pending doctors
4. Approve a doctor
5. Test doctor login with approved credentials

## Troubleshooting

### "Registration failed" error
- Check if uploads directory exists and is writable
- Check file size (max 5MB)
- Check file type (PDF or images only)

### "No pending verifications" in Admin Dashboard
- Check database: `SELECT * FROM doctors WHERE status = 'pending'`
- Verify API endpoint is working: `http://localhost/VaidyaCare/api/get_pending_doctors.php`

### Doctor cannot login after approval
- Check doctor status in database: `SELECT status FROM doctors WHERE email = '...'`
- Status must be `approved` for login to work

