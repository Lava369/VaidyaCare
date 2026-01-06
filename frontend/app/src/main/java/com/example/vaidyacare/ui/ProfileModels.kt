package com.example.vaidyacare.network

data class ProfileResponse(
    val success: Boolean,
    val data: ProfileData
)

data class ProfileData(
    val full_name: String,
    val email: String,
    val mobile: String,
    val dob: String?,
    val gender: String?,
    val blood_group: String?,
    val height_cm: String?,
    val weight_kg: String?,
    val address: String?,
    val city: String?,
    val state: String?,
    val pin: String?,
    val patient_id: String? = null
)
