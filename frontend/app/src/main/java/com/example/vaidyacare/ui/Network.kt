package com.example.vaidyacare.network

data class LoginRequest(
    val email: String? = null,
    val mobile: String? = null,
    val password: String
)
