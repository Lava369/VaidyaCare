package com.example.vaidyacare.network

data class SignupRequest(
    val full_name: String,
    val email: String,
    val mobile: String,
    val age: Int,
    val gender: String,
    val password: String
)
