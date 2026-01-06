package com.example.vaidyacare.network

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(
    @SerializedName("email_or_mobile")
    val emailOrMobile: String,
    @SerializedName("new_password")
    val newPassword: String
)
