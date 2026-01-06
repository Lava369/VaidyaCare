package com.example.vaidyacare.network

data class SaveProfileRequest(
    val full_name: String,
    val email: String,
    val mobile: String,
    val dob: String?,
    val gender: String?,
    val blood_group: String?,
    val height: String?,
    val weight: String?,
    val address: String?,
    val city: String?,
    val state: String?,
    val pin: String?
)
