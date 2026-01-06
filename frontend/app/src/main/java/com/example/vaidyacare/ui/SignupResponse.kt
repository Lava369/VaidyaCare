package com.example.vaidyacare.network

data class SignupResponse(
    val success: Boolean,
    val message: String?,
    val data: SignupData?
)

data class SignupData(
    val user_id: Int,
    val full_name: String,
    val email: String,
    val mobile: String
)
