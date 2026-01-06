
package com.example.vaidyacare.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.vaidyacare.R

// import Doctor data class & sample list
import com.example.vaidyacare.ui.Doctor
import com.example.vaidyacare.ui.sampleDoctors
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

@Composable
fun ConsultNowScreen(
    navController: NavController,
    onBack: () -> Unit = { navController.popBackStack() }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val client = remember { OkHttpClient() }

    val tabs = listOf("All Doctors", "Online", "Busy", "Offline")
    var selectedTab by remember { mutableStateOf("All Doctors") }
    var doctors by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }

    val bg = Brush.verticalGradient(listOf(Color(0xFFF5F9FF), Color.White))

    // Load doctors from backend - refresh when tab changes
    LaunchedEffect(selectedTab) {
        loading = true
        val statusFilter = when (selectedTab) {
            "Online", "Busy", "Offline" -> selectedTab
            else -> ""
        }
        loadDoctors(client, scope, statusFilter) { doctorList ->
            doctors = doctorList
            loading = false
            android.util.Log.d("ConsultNow", "Loaded ${doctorList.size} doctors for filter: $statusFilter")
        }
    }

    Scaffold(
        topBar = { ConsultTopBar(onBack) },
        bottomBar = { HomeBottomNavBar(navController) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            SearchBar(searchQuery) { searchQuery = it }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                tabs.forEach { tab ->
                    DoctorFilterChip(
                        text = tab,
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Available Doctors", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF47C776))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Online now", color = Color.Gray, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter by search query
            val filtered = doctors.filter {
                if (searchQuery.isBlank()) true
                else it.name.contains(searchQuery, ignoreCase = true) ||
                     it.specialization.contains(searchQuery, ignoreCase = true)
            }

            LazyColumn {
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
                } else if (filtered.isEmpty()) {
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
                    items(filtered) { doctor ->
                        DoctorCard(doctor)
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
        }
    }
}

/* ----------------------------- TOP BAR ---------------------------- */

@Composable
fun ConsultTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Text(
            "Consult Now",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

/* --------------------------- SEARCH BAR ---------------------------- */

@Composable
fun SearchBar(
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF2F3F5)),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            Icons.Default.Search,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.padding(start = 12.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search doctors, specialties...", color = Color.Gray) },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = {}) {
            Icon(painterResource(id = R.drawable.ic_filter), contentDescription = "Filter")
        }
    }
}

/* --------------------------- FILTER CHIP --------------------------- */

@Composable
fun DoctorFilterChip(text: String, selected: Boolean, onClick: () -> Unit) {

    val background = if (selected) Color(0xFF2F6BFF) else Color.White
    val textColor = if (selected) Color.White else Color.Black

    val chipModifier = if (selected) {
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    } else {
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .border(1.dp, Color(0xFFE6E6E6), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    }

    Box(modifier = chipModifier) {
        Text(text, fontSize = 13.sp, color = textColor)
    }
}

/* --------------------------- DOCTOR CARD --------------------------- */

@Composable
fun DoctorCard(
    doctor: Doctor,
    onConsultClick: (Doctor) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onConsultClick(doctor) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                // Profile image - use backend image if available, otherwise use placeholder
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3F2FD)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!doctor.profileImage.isNullOrEmpty()) {
                        // Use AsyncImage for network images
                        AsyncImage(
                            model = "http://10.26.77.190${doctor.profileImage}",
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        // Show initials as placeholder
                        Text(
                            doctor.name.take(2).uppercase(),
                            color = Color(0xFF1976D2),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = doctor.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = doctor.specialization,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    // Experience and Fee with clock icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_time),
                            contentDescription = "Time",
                            modifier = Modifier.size(14.dp),
                            colorFilter = ColorFilter.tint(Color.Gray)
                        )
                        Text(
                            text = "${doctor.experience} years",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "₹${doctor.fee}",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Rating on the right side
                RatingPill(doctor.rating)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Status badge
            StatusBadge(doctor.status)

            Spacer(modifier = Modifier.height(10.dp))

            // Consult Now button
            Button(
                onClick = { onConsultClick(doctor) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF2F6BFF))
            ) {
                Text("Consult Now", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/* ------------------------------ RATING ------------------------------ */

@Composable
fun RatingPill(rating: Double) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFFFF2CC))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("⭐", fontSize = 12.sp)
            Text("$rating", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

/* ------------------------------ STATUS ------------------------------ */

@Composable
fun StatusBadge(status: String) {

    val (bg, textColor) = when (status) {
        "Online" -> Color(0xFFDFF7EE) to Color(0xFF3ABF81)
        "Busy" -> Color(0xFFFFE6C8) to Color(0xFFD57A00)
        else -> Color(0xFFE8E8E8) to Color.Gray
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(status, color = textColor, fontSize = 12.sp)
    }
}

/* --------------------------- API FUNCTIONS ---------------------------- */

private fun loadDoctors(
    client: OkHttpClient,
    scope: kotlinx.coroutines.CoroutineScope,
    statusFilter: String = "",
    onResult: (List<Doctor>) -> Unit
) {
    val baseUrl = "http://10.26.77.190/vaidyacare/api/get_all_doctors.php"
    val url = if (statusFilter.isNotEmpty()) {
        "$baseUrl?status=${java.net.URLEncoder.encode(statusFilter, "UTF-8")}"
    } else {
        baseUrl
    }

    android.util.Log.d("ConsultNow", "Loading doctors from: $url")
    
    val request = Request.Builder()
        .url(url)
        .get()
        .addHeader("Content-Type", "application/json")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            android.util.Log.e("ConsultNow", "Network error: ${e.message}", e)
            scope.launch {
                onResult(emptyList())
            }
        }

        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string().orEmpty()
            scope.launch {
                try {
                    android.util.Log.d("ConsultNow", "Response code: ${response.code}")
                    android.util.Log.d("ConsultNow", "Response body: $body")
                    
                    if (response.code != 200) {
                        android.util.Log.e("ConsultNow", "HTTP Error: ${response.code}")
                        onResult(emptyList())
                        return@launch
                    }
                    
                    if (body.isEmpty()) {
                        android.util.Log.e("ConsultNow", "Empty response body")
                        onResult(emptyList())
                        return@launch
                    }
                    
                    val json = JSONObject(body)
                    val success = json.optBoolean("success", false)
                    android.util.Log.d("ConsultNow", "Success: $success")
                    
                    if (success) {
                        val dataArray = json.optJSONArray("data")
                        if (dataArray != null) {
                            val doctorList = mutableListOf<Doctor>()
                            android.util.Log.d("ConsultNow", "Found ${dataArray.length()} doctors")
                            
                            for (i in 0 until dataArray.length()) {
                                try {
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
                                } catch (e: Exception) {
                                    android.util.Log.e("ConsultNow", "Error parsing doctor $i: ${e.message}", e)
                                }
                            }
                            android.util.Log.d("ConsultNow", "Successfully parsed ${doctorList.size} doctors")
                            onResult(doctorList)
                        } else {
                            android.util.Log.e("ConsultNow", "No data array in response")
                            onResult(emptyList())
                        }
                    } else {
                        val message = json.optString("message", "Unknown error")
                        android.util.Log.e("ConsultNow", "API Error: $message")
                        onResult(emptyList())
                    }
                } catch (e: Exception) {
                    android.util.Log.e("ConsultNow", "Error loading doctors: ${e.message}", e)
                    android.util.Log.e("ConsultNow", "Response body was: $body")
                    onResult(emptyList())
                }
            }
        }
    })
}
