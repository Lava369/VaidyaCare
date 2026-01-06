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
