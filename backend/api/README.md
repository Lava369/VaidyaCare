# VaidyaCare API Setup Instructions

## Database Setup

1. Open phpMyAdmin: http://localhost/phpmyadmin
2. Import or run the SQL file: `database_setup.sql`
   - This will create the `vaidyacare_db` database
   - This will create the `users` table with all required fields

## API Endpoints

### Signup Endpoint
- **URL**: `http://10.26.77.190/vaidyacare/api/signup.php`
- **Method**: POST
- **Content-Type**: application/json

### Request Body:
```json
{
    "full_name": "John Doe",
    "email": "john@example.com",
    "mobile": "+917416939192",
    "age": 25,
    "gender": "Male",
    "password": "password123"
}
```

### Success Response:
```json
{
    "success": true,
    "message": "Signup successful",
    "data": {
        "user_id": 1,
        "full_name": "John Doe",
        "email": "john@example.com",
        "mobile": "+917416939192",
        "patient_id": "PAT000001"
    }
}
```

### Error Response:
```json
{
    "success": false,
    "message": "Error message here"
}
```

## Configuration

- **Database Host**: localhost
- **Database Name**: vaidyacare_db
- **Database User**: root
- **Database Password**: (empty by default in XAMPP)

If you need to change database credentials, edit the `signup.php` file:
```php
$host = 'localhost';
$dbname = 'vaidyacare_db';
$username = 'root';
$password_db = '';
```

## Testing

You can test the API using:
1. Postman
2. cURL
3. The Android app

Make sure:
- XAMPP Apache and MySQL are running
- The database is created and the table exists
- The API folder is accessible at: `http://10.26.77.190/vaidyacare/api/`

