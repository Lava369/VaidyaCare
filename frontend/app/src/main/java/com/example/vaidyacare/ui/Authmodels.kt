package com.example.vaidyacare.ui.network

import com.google.gson.annotations.SerializedName

/* ================= LOGIN ================= */
data class LoginRequest(
    @SerializedName("email_or_mobile")
    val emailOrMobile: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    @SerializedName("user_id")
    val userId: Int? = null
)

/* ================= FORGOT PASSWORD ================= */
data class ForgotPasswordRequest(
    val email: String
)

data class ForgotPasswordResponse(
    val success: Boolean,
    val message: String,
    @SerializedName("user_id")
    val userId: Int
)

/* ================= VERIFY OTP ================= */
data class VerifyOtpRequest(
    @SerializedName("user_id")
    val userId: Int,
    val otp: String
)

data class VerifyOtpResponse(
    val success: Boolean,
    val message: String
)

/* ================= RESET PASSWORD ================= */
data class ResetPasswordRequest(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("new_password")
    val newPassword: String
)

data class ResetPasswordResponse(
    val success: Boolean,
    val message: String
)

/* ================= COMMON RESPONSE ================= */
data class CommonResponse(
    val success: Boolean,
    val message: String
)
