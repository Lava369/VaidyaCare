package com.example.vaidyacare.network

/* ================= DOCTOR LOGIN ================= */

data class DoctorLoginRequest(
    val email: String,
    val password: String
)

data class DoctorLoginResponse(
    val success: Boolean,
    val message: String,
    val doctor_id: Int? = null
)
