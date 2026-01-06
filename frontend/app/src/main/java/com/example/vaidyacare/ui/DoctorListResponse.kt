package com.example.vaidyacare.network

import com.google.gson.annotations.SerializedName

data class DoctorListResponse(
    val success: Boolean,
    val data: List<DoctorApiModel> = emptyList()
)

data class DoctorApiModel(
    @SerializedName("doctor_id")
    val doctorId: Int,

    @SerializedName("full_name")
    val name: String,

    @SerializedName("specialization")
    val specialization: String,

    @SerializedName("experience_years")
    val experience: String,

    @SerializedName("clinic_name")
    val clinic: String,

    @SerializedName("registration_number")
    val regNo: String
)