package com.example.vaidyacare.network

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    /* ================= USER SIGNUP ================= */
    @POST("signup.php")
    fun signup(
        @Body request: SignupRequest
    ): Call<SignupResponse>

    /* ================= USER LOGIN ================= */
    @POST("login.php")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    /* ================= DOCTOR LOGIN (✅ ADDED CORRECTLY) ================= */
    @POST("doctor_login.php")
    fun doctorLogin(
        @Body body: RequestBody
    ): Call<DoctorLoginResponse>

    /* ================= ADMIN LOGIN ================= */
    @POST("admin_login.php")
    fun adminLogin(
        @Body request: AdminLoginRequest
    ): Call<AdminLoginResponse>

    /* ================= FORGOT PASSWORD ================= */
    @POST("send_reset_otp.php")
    fun sendResetOtp(
        @Body request: ForgotPasswordRequest
    ): Call<CommonResponse>

    /* ================= VERIFY OTP ================= */
    @POST("verify_otp.php")
    fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): Call<CommonResponse>

    /* ================= RESET PASSWORD ================= */
    @POST("reset_password.php")
    fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Call<CommonResponse>

    /* ================= ADMIN DASHBOARD ================= */
    @GET("get_pending_doctors.php")
    fun getPendingDoctors(): Call<DoctorListResponse>

    @FormUrlEncoded
    @POST("approve_doctor.php")
    fun approveDoctor(
        @Field("doctor_id") doctorId: Int
    ): Call<CommonResponse>

    @FormUrlEncoded
    @POST("reject_doctor.php")
    fun rejectDoctor(
        @Field("doctor_id") doctorId: Int
    ): Call<CommonResponse>

    /* ================= GET USER PROFILE ================= */
    /* ================= GET PATIENT PROFILE (✅ FIXED) ================= */
    @POST("patientSave_profile.php")
    suspend fun saveProfile(
        @Body body: Map<String, String>
    ): CommonResponse

    @GET("patientget_profile.php")
    suspend fun getProfile(
        @Query("email") email: String
    ): ProfileResponse
}
