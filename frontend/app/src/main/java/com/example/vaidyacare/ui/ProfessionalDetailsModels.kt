package com.example.vaidyacare.network

/* ---------- GET RESPONSE ---------- */
data class GetProfessionalResponse(
    val success: Boolean,
    val data: ProfessionalDetailsData?
)

/* ---------- DATA ---------- */
data class ProfessionalDetailsData(
    val doctor_id: Int,
    val primary_specialization: String,
    val sub_specialization: String,
    val experience_years: Int,
    val license_number: String,
    val medical_council: String,
    val qualifications: String,
    val languages: String,
    val consultation_fee: Double
)

/* ---------- SAVE REQUEST ---------- */
data class ProfessionalDetailsRequest(
    val doctor_id: Int,
    val primary_specialization: String,
    val sub_specialization: String,
    val experience_years: Int,
    val license_number: String,
    val medical_council: String,
    val qualifications: String,
    val languages: String,
    val consultation_fee: Double
)
