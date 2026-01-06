data class DoctorLoginResponse(
    val success: Boolean,
    val message: String,
    val doctor: DoctorData?
)

data class DoctorData(
    val doctor_id: Int,
    val full_name: String,
    val email: String,
    val mobile: String,
    val license_no: String
)
