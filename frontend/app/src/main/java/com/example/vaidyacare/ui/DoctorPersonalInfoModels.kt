package com.example.vaidyacare.network

/* ---------- PERSONAL INFO RESPONSE ---------- */
data class DoctorPersonalInfoResponse(
    val success: Boolean,
    val data: DoctorPersonalInfoData?
)

data class DoctorPersonalInfoData(
    val full_name: String,
    val email: String,
    val phone: String,
    val location: String,
    val birth_month: String,
    val gender: String,
    val profile_image: String?
)

/* ---------- UPDATE REQUEST ---------- */
data class UpdateDoctorPersonalInfoRequest(
    val doctor_id: Int,
    val full_name: String,
    val email: String,
    val phone: String,
    val location: String,
    val birth_month: String,
    val gender: String
)

/* ---------- UPLOAD RESPONSE ---------- */
data class UploadPhotoResponse(
    val success: Boolean,
    val image: String?
)
