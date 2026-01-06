@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.example.vaidyacare.utils.DoctorSession
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

/* -------------------------------------------------
   DOCTOR PROFESSIONAL DETAILS SCREEN
------------------------------------------------- */

@Composable
fun DoctorProfessionalDetailsScreen(
    doctorId: Int? = null, // Optional: can be passed from navigation or retrieved from session
    onBack: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val client = remember { OkHttpClient() }

    // Get doctor_id from parameter or session
    val currentDoctorId = remember(doctorId) {
        val sessionId = DoctorSession.getDoctorId(context)
        doctorId ?: sessionId
    }

    var loading by remember { mutableStateOf(false) }
    var loadingData by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Validate doctor_id
    if (currentDoctorId <= 0) {
        LaunchedEffect(Unit) {
            errorMessage = "Doctor ID not found. Please login again."
            loadingData = false
        }
    }

    // Form fields
    var fullName by remember { mutableStateOf("") }
    var doctorMode by remember { mutableStateOf("Offline") }
    var primarySpecialization by remember { mutableStateOf("") }
    var subSpecialization by remember { mutableStateOf("") }
    var experienceYears by remember { mutableStateOf("") }
    var medicalLicenseNumber by remember { mutableStateOf("") }
    var medicalCouncil by remember { mutableStateOf("") }
    var qualifications by remember { mutableStateOf("") }
    var languagesSpoken by remember { mutableStateOf("") }
    var consultationFee by remember { mutableStateOf("") }

    // Load existing data
    LaunchedEffect(currentDoctorId) {
        if (currentDoctorId > 0) {
            loadProfessionalDetails(currentDoctorId, client, scope) { data ->
                fullName = data["full_name"] ?: ""
                doctorMode = data["doctor_mode"] ?: "Offline"
                primarySpecialization = data["primary_specialization"] ?: ""
                subSpecialization = data["sub_specialization"] ?: ""
                experienceYears = data["experience_years"] ?: ""
                medicalLicenseNumber = data["medical_license_number"] ?: ""
                medicalCouncil = data["medical_council"] ?: ""
                qualifications = data["qualifications"] ?: ""
                languagesSpoken = data["languages_spoken"] ?: ""
                consultationFee = data["consultation_fee"] ?: "0"
                loadingData = false
                errorMessage = null
            }
        } else {
            loadingData = false
            errorMessage = "Failed to load professional details"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Professional Details",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Update your professional information",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF22C55E)
                )
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    if (currentDoctorId <= 0) {
                        Toast.makeText(context, "Invalid Doctor ID. Please login again.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    scope.launch {
                        loading = true
                        errorMessage = null
                        saveProfessionalDetails(
                            currentDoctorId,
                            fullName,
                            doctorMode,
                            primarySpecialization,
                            subSpecialization,
                            experienceYears,
                            medicalLicenseNumber,
                            medicalCouncil,
                            qualifications,
                            languagesSpoken,
                            consultationFee,
                            client,
                            scope
                        ) { success, message ->
                            loading = false
                            if (success) {
                                Toast.makeText(context, "Professional details saved successfully!", Toast.LENGTH_SHORT).show()
                                // Reload data to show updated information
                                loadProfessionalDetails(currentDoctorId, client, scope) { data ->
                                    fullName = data["full_name"] ?: ""
                                    doctorMode = data["doctor_mode"] ?: "Offline"
                                    primarySpecialization = data["primary_specialization"] ?: ""
                                    subSpecialization = data["sub_specialization"] ?: ""
                                    experienceYears = data["experience_years"] ?: ""
                                    medicalLicenseNumber = data["medical_license_number"] ?: ""
                                    medicalCouncil = data["medical_council"] ?: ""
                                    qualifications = data["qualifications"] ?: ""
                                    languagesSpoken = data["languages_spoken"] ?: ""
                                    consultationFee = data["consultation_fee"] ?: "0"
                                    errorMessage = null
                                }
                                onSave()
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                enabled = !loading && !loadingData && currentDoctorId > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF22C55E)
                )
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Save Changes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = errorMessage ?: "Error",
                            color = Color(0xFFD32F2F),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Current Doctor ID: $currentDoctorId",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            } else if (loadingData) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                sectionTitle("Basic Information")
                editableField("Full Name *", fullName) { fullName = it }
                
                Spacer(modifier = Modifier.height(16.dp))
                sectionTitle("Doctor Mode")
                doctorModeSelector(
                    selectedMode = doctorMode,
                    onModeSelected = { doctorMode = it }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                sectionTitle("Specialization")
                editableField("Primary Specialization *", primarySpecialization) { primarySpecialization = it }
                editableField("Sub-Specialization", subSpecialization) { subSpecialization = it }

                Spacer(modifier = Modifier.height(16.dp))
                sectionTitle("Experience & Credentials")
                editableField("Years of Experience *", experienceYears) { experienceYears = it }
                editableField("Medical License Number *", medicalLicenseNumber) { medicalLicenseNumber = it }
                editableField("Medical Council *", medicalCouncil) { medicalCouncil = it }
                editableField("Qualifications *", qualifications) { qualifications = it }

                Spacer(modifier = Modifier.height(16.dp))
                sectionTitle("Additional Information")
                editableField("Languages Spoken", languagesSpoken) { languagesSpoken = it }
                editableField("Consultation Fee (₹)", consultationFee) { consultationFee = it }
            }
        }
    }
}

/* -------------------------------------------------
   HELPER COMPOSABLES
------------------------------------------------- */

@Composable
private fun sectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun infoField(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {

        Text(
            text = label,
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFF6F6F6),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(14.dp)
        ) {
            Text(
                text = value,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun editableField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun doctorModeSelector(
    selectedMode: String,
    onModeSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        listOf("Online", "Offline", "Busy").forEach { mode ->

            val isSelected = selectedMode == mode

            Button(
                onClick = { onModeSelected(mode) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFF22C55E) else Color(0xFFF1F5F9)
                )
            ) {
                Text(
                    text = mode,
                    color = if (isSelected) Color.White else Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/* -------------------------------------------------
   API FUNCTIONS
------------------------------------------------- */

private fun loadProfessionalDetails(
    doctorId: Int,
    client: OkHttpClient,
    scope: kotlinx.coroutines.CoroutineScope,
    onResult: (Map<String, String>) -> Unit
) {
    val request = Request.Builder()
        .url("http://10.26.77.190/vaidyacare/api/get_doctor_professional_details.php?doctor_id=$doctorId")
        .get()
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            scope.launch {
                onResult(emptyMap())
            }
        }

        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string().orEmpty()
            scope.launch {
                try {
                    val json = JSONObject(body)
                    if (json.optBoolean("success")) {
                        val data = json.getJSONObject("data")
                        onResult(mapOf(
                            "full_name" to data.optString("full_name", ""),
                            "doctor_mode" to data.optString("doctor_mode", "Offline"),
                            "primary_specialization" to data.optString("primary_specialization", ""),
                            "sub_specialization" to data.optString("sub_specialization", ""),
                            "experience_years" to data.optString("experience_years", ""),
                            "medical_license_number" to data.optString("medical_license_number", ""),
                            "medical_council" to data.optString("medical_council", ""),
                            "qualifications" to data.optString("qualifications", ""),
                            "languages_spoken" to data.optString("languages_spoken", ""),
                            "consultation_fee" to data.optString("consultation_fee", "0")
                        ))
                    } else {
                        onResult(emptyMap())
                    }
                } catch (e: Exception) {
                    onResult(emptyMap())
                }
            }
        }
    })
}

private fun saveProfessionalDetails(
    doctorId: Int,
    fullName: String,
    doctorMode: String,
    primarySpecialization: String,
    subSpecialization: String,
    experienceYears: String,
    medicalLicenseNumber: String,
    medicalCouncil: String,
    qualifications: String,
    languagesSpoken: String,
    consultationFee: String,
    client: OkHttpClient,
    scope: kotlinx.coroutines.CoroutineScope,
    onResult: (Boolean, String) -> Unit
) {
    val json = JSONObject().apply {
        put("doctor_id", doctorId)
        put("full_name", fullName)
        put("doctor_mode", doctorMode)
        put("primary_specialization", primarySpecialization)
        put("sub_specialization", subSpecialization)
        put("experience_years", experienceYears)
        put("medical_license_number", medicalLicenseNumber)
        put("medical_council", medicalCouncil)
        put("qualifications", qualifications)
        put("languages_spoken", languagesSpoken)
        put("consultation_fee", consultationFee.toDoubleOrNull() ?: 0.0)
    }

    val body = json.toString().toRequestBody("application/json".toMediaType())

    val request = Request.Builder()
        .url("http://10.26.77.190/vaidyacare/api/update_doctor_professional_details.php")
        .post(body)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            scope.launch {
                onResult(false, "Network error: ${e.message}")
            }
        }

        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string().orEmpty()
            scope.launch {
                try {
                    if (response.code == 200 && body.isNotEmpty()) {
                        val json = JSONObject(body)
                        val success = json.optBoolean("success")
                        val message = json.optString("message", if (success) "Saved successfully" else "Failed to save")
                        onResult(success, message)
                    } else {
                        // Handle non-200 responses
                        val errorMsg = if (body.isNotEmpty()) {
                            try {
                                val json = JSONObject(body)
                                json.optString("message", "Failed to save. Error code: ${response.code}")
                            } catch (_: Exception) {
                                "Failed to save. Error code: ${response.code}"
                            }
                        } else {
                            "Failed to save. Error code: ${response.code}"
                        }
                        onResult(false, errorMsg)
                    }
                } catch (e: Exception) {
                    onResult(false, "Error parsing response: ${e.localizedMessage ?: "Please try again"}")
                }
            }
        }
    })
}
