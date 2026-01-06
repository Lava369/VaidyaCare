package com.example.vaidyacare.network

/* ================= ADMIN APPROVE / REJECT ================= */

data class ApproveDoctorRequest(
    val doctor_id: Int
)

data class RejectDoctorRequest(
    val doctor_id: Int
)
