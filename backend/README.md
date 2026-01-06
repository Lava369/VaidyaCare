# VaidyaCare Backend API

## Setup Instructions

### 1. Database Setup

1. Open phpMyAdmin: `http://localhost/phpmyadmin`
2. Select database: `vaidyacare_db`
3. Import or run the SQL script: `database/create_admin_table.sql`
4. Alternatively, access `database/insert_admin.php` via browser to create admin user

### 2. Database Configuration

Edit `api/config.php` if your MySQL credentials are different:
- Default: `localhost`, `root`, no password
- Database: `vaidyacare_db`

### 3. Default Admin Credentials

- **Email**: `admin@vaidyacare.com`
- **Password**: `admin123`

**⚠️ IMPORTANT**: Change the default password after first login!

### 4. API Endpoints

#### Admin Login
- **URL**: `http://localhost/VaidyaCare/api/admin_login.php`
- **Method**: `POST`
- **Content-Type**: `application/json`
- **Request Body**:
```json
{
    "email": "admin@vaidyacare.com",
    "password": "admin123"
}
```
- **Success Response** (200):
```json
{
    "success": true,
    "message": "Login successful.",
    "data": {
        "admin_id": 1,
        "email": "admin@vaidyacare.com",
        "name": "Admin User"
    }
}
```
- **Error Response** (401):
```json
{
    "success": false,
    "message": "Invalid email or password."
}
```

### 5. Postman Collection

Import `Postman_Collection_VaidyaCare_Admin.json` into Postman to test the API.

### 6. Testing

1. Start XAMPP (Apache and MySQL)
2. Test via Postman or browser
3. For Android app, update base URL in `RetrofitClient.kt`:
   - Local: `http://10.0.2.2/VaidyaCare/api/` (Android Emulator)
   - Network: `http://YOUR_IP/VaidyaCare/api/` (Real Device)

### 7. File Structure

```
VaidyaCare/
├── api/
│   ├── config.php          (Database connection)
│   └── admin_login.php     (Admin login endpoint)
├── database/
│   ├── create_admin_table.sql  (SQL script)
│   └── insert_admin.php        (Admin creation script)
├── Postman_Collection_VaidyaCare_Admin.json
└── README.md
```

## Security Notes

- Passwords are hashed using PHP's `password_hash()` function
- Always use HTTPS in production
- Implement rate limiting for login attempts
- Add session management for authenticated requests
- Sanitize all inputs (already implemented with prepared statements)
