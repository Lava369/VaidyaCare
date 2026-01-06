
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.vaidyacare.R

// Import StatusBadge from ConsultNow
import com.example.vaidyacare.ui.StatusBadge
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

@Composable
fun FindDoctorScreen(
    onBack: () -> Unit,
    onBySpecialtyClick: () -> Unit,
    onBookAppointment: (Doctor) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val client = remember { OkHttpClient() }
    
    var query by remember { mutableStateOf("") }
    var doctors by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    val bgGradient = Brush.verticalGradient(
        listOf(Color(0xFFF5F9FF), Color.White)
    )

    // Load doctors from backend
    LaunchedEffect(Unit) {
        loadDoctors(client, scope) { doctorList ->
            doctors = doctorList
            loading = false
        }
    }

    val filteredDoctors = remember(query, doctors) {
        if (query.isBlank()) {
            doctors
        } else {
            doctors.filter {
                it.name.contains(query, true) ||
                it.specialization.contains(query, true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Find Doctors",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
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

            /* 🔍 SEARCH */
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    placeholder = { Text("Search doctors, specialties...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, null, tint = Color.Gray)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color(0xFF2F6BFF),
                        unfocusedContainerColor = Color(0xFFF2F3F5),
                        focusedContainerColor = Color.White
                    )
                )
            }

            /* ⚡ QUICK ACTIONS */
            item {
                Text("Quick Actions", fontWeight = FontWeight.Bold)
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionBox(
                        iconId = R.drawable.ic_calendar,
                        title = "By Specialty",
                        modifier = Modifier.weight(1f),
                        onClick = onBySpecialtyClick
                    )

                    QuickActionBox(
                        iconId = R.drawable.ic_trophy,
                        title = "Top Rated",
                        modifier = Modifier.weight(1f),
                        onClick = {}
                    )
                }
            }

            /* 👨‍⚕️ DOCTORS */
            item {
                Text("Featured Doctors", fontWeight = FontWeight.Bold)
            }

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
            } else if (filteredDoctors.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No doctors found", color = Color.Gray)
                    }
                }
            } else {
                items(filteredDoctors) { doctor ->
                    DoctorListCard(
                        doctor = doctor,
                        onBook = onBookAppointment
                    )
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

/* ===================================================
   COMPONENTS
=================================================== */

@Composable
private fun QuickActionBox(
    iconId: Int,
    title: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(84.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(painterResource(iconId), null, Modifier.size(28.dp))
            Spacer(Modifier.height(6.dp))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

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

                // Profile image - use backend image if available, otherwise use placeholder
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE3F2FD)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!doctor.profileImage.isNullOrEmpty()) {
                        AsyncImage(
                            model = "http://10.26.77.190${doctor.profileImage}",
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            doctor.name.take(2).uppercase(),
                            color = Color(0xFF1976D2),
                            fontWeight = FontWeight.Bold
                        )
                    }
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

/* --------------------------- API FUNCTIONS ---------------------------- */

private fun loadDoctors(
    client: OkHttpClient,
    scope: kotlinx.coroutines.CoroutineScope,
    onResult: (List<Doctor>) -> Unit
) {
    val request = Request.Builder()
        .url("http://10.26.77.190/vaidyacare/api/get_all_doctors.php")
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
                } catch (e: Exception) {
                    onResult(emptyList())
                }
            }
        }
    })
}
