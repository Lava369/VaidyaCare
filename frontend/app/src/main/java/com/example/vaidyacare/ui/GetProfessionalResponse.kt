data class GetProfessionalResponse(
    val success: Boolean,
    val data: ProfessionalDetailsData?
)

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
