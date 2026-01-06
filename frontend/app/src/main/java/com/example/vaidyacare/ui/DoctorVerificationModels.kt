package com.example.vaidyacare.network

data class VerificationStatusResponse(
    val success: Boolean,
    val data: StatusData?
)

data class StatusData(
    val status: String
)

data class PendingDoctorsResponse(
    val success: Boolean,
    val data: List<PendingDoctor>
)

data class PendingDoctor(
    val verification_id: Int,
    val doctor_id: Int,
    val name: String,
    val specialization: String,
    val experience: String,
    val clinic: String,
    val reg_no: String,
    val status: String
)
