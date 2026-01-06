package com.example.vaidyacare.network

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user: UserData? = null
)

data class UserData(
    val id: Int,
    val full_name: String,
    val email: String,
    val mobile: String
)
