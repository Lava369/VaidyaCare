package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

// Import StatusBadge from ConsultNow
import com.example.vaidyacare.ui.StatusBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecialtyDoctorsScreen(
    navController: NavController,
    specialization: String
) {
    val scope = rememberCoroutineScope()
    val client = remember { OkHttpClient() }
    
    var doctors by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    val bgGradient = Brush.verticalGradient(
        listOf(Color(0xFFF5F9FF), Color.White)
    )

    // Load doctors by specialization
    LaunchedEffect(specialization) {
        loading = true
        loadDoctorsBySpecialization(client, scope, specialization) { doctorList ->
            doctors = doctorList
            loading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        specialization,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (doctors.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No doctors found for $specialization", color = Color.Gray)
                    }
                }
            } else {
                item {
                    Text(
                        "${doctors.size} doctors available",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(doctors) { doctor ->
                    DoctorListCard(
                        doctor = doctor,
                        onBook = { navController.navigate("bookAppointmentFlow") }
                    )
                }
            }
            item { Spacer(Modifier.height(60.dp)) }
        }
    }
}

/* --------------------------- API FUNCTIONS ---------------------------- */

private fun loadDoctorsBySpecialization(
    client: OkHttpClient,
    scope: kotlinx.coroutines.CoroutineScope,
    specialization: String,
    onResult: (List<Doctor>) -> Unit
) {
    val encodedSpecialization = java.net.URLEncoder.encode(specialization, "UTF-8")
    val request = Request.Builder()
        .url("http://10.26.77.190/vaidyacare/api/get_doctors_by_specialization.php?specialization=$encodedSpecialization")
        .get()
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            scope.launch {
                onResult(emptyList())
            }
        }

        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string().orEmpty()
            scope.launch {
                try {
                    val json = JSONObject(body)
                    if (json.optBoolean("success")) {
                        val dataArray = json.getJSONArray("data")
                        val doctorList = mutableListOf<Doctor>()
                        for (i in 0 until dataArray.length()) {
                            val doctorJson = dataArray.getJSONObject(i)
                            val doctorName = doctorJson.optString("name", "").takeIf { it.isNotEmpty() } ?: "Doctor"
                            val doctorSpecialization = doctorJson.optString("specialization", "").takeIf { it.isNotEmpty() } ?: "General Medicine"
                            val doctorExperience = doctorJson.optInt("experience", 0)
                            val doctorFee = doctorJson.optInt("fee", 0)
                            val doctorStatus = doctorJson.optString("status", "Offline").takeIf { it.isNotEmpty() } ?: "Offline"
                            
                            doctorList.add(
                                Doctor(
                                    name = doctorName,
                                    specialization = doctorSpecialization,
                                    experience = doctorExperience,
                                    fee = doctorFee,
                                    rating = doctorJson.optDouble("rating", 4.5),
                                    status = doctorStatus,
                                    doctorId = doctorJson.optInt("doctor_id", 0),
                                    profileImage = doctorJson.optString("profile_image", "").takeIf { it.isNotEmpty() }
                                )
                            )
                        }
                        onResult(doctorList)
                    } else {
                        onResult(emptyList())
                    }
                } catch (_: Exception) {
                    onResult(emptyList())
                }
            }
        }
    })
}

/* --------------------------- COMPONENTS ---------------------------- */

@Composable
private fun DoctorListCard(
    doctor: Doctor,
    onBook: (Doctor) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(Modifier.padding(14.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                // Profile image placeholder - can be enhanced with actual image loading
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFFE3F2FD), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        doctor.name.take(2).uppercase(),
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text(doctor.name, fontWeight = FontWeight.Bold)
                    Text(
                        doctor.specialization,
                        color = Color(0xFF2F6BFF),
                        fontSize = 13.sp
                    )
                    Text(
                        "★ ${doctor.rating} • ${doctor.experience} yrs",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Text("₹${doctor.fee}", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(8.dp))

            // Display status badge
            StatusBadge(doctor.status)

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { onBook(doctor) },
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Book Appointment")
            }
        }
    }
}
