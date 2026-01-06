
package com.example.vaidyacare.ui

import com.example.vaidyacare.R

data class Doctor(
    val name: String,
    val specialization: String,
    val experience: Int,
    val fee: Int,
    val image: Int = R.drawable.ic_user_blue, // Default image
    val rating: Double,
    val status: String,
    val doctorId: Int = 0, // Added for backend data
    val profileImage: String? = null // Added for profile image URL
)

val sampleDoctors = listOf(
    Doctor(
        "Dr. Priya Sharma",
        "General Physician",
        12,
        500,
        R.drawable.ic_user_blue,
        4.8,
        "Online"
    ),
    Doctor(
        "Dr. Rajesh Kumar",
        "Cardiologist",
        15,
        800,
        R.drawable.ic_user_blue,
        4.9,
        "Busy"
    ),
    Doctor(
        "Dr. Anita Desai",
        "Dermatologist",
        10,
        600,
        R.drawable.ic_user_blue,
        4.7,
        "Offline"
    )
)
