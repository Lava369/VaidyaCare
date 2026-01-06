
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.vaidyacare.utils.DoctorSession
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import android.net.Uri

/* ---------------- MAIN SCREEN ---------------- */

@Composable
fun DoctorProfileScreen(
    navController: NavController,
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val client = remember { OkHttpClient() }
    
    // Get doctor_id from session
    val doctorId = remember { DoctorSession.getDoctorId(context) }
    
    var loading by remember { mutableStateOf(true) }
    var fullName by remember { mutableStateOf("") }
    var specialization by remember { mutableStateOf("General Medicine") }
    var initials by remember { mutableStateOf("DR") }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    
    // Load profile data
    LaunchedEffect(doctorId) {
        if (doctorId > 0) {
            loadDoctorProfile(doctorId, client, scope) { data ->
                fullName = data["full_name"] ?: ""
                specialization = data["specialization"] ?: "General Medicine"
                initials = data["initials"] ?: "DR"
                profileImageUrl = data["profile_image"]?.takeIf { it.isNotEmpty() }
                loading = false
            }
        } else {
            loading = false
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            DoctorBottomBar(navController)
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF5F5F5))
        ) {

            /* ---------------- HEADER ---------------- */

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF2ECC71),
                                Color(0xFF27AE60)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        if (loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(72.dp),
                                color = Color.White
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2ECC71)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (profileImageUrl != null) {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            Uri.parse("http://10.26.77.190$profileImageUrl")
                                        ),
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(
                                        text = initials,
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (loading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Text(
                                text = if (fullName.isNotEmpty()) fullName else "Loading...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )

                            Text(
                                text = specialization,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileStat("4.8", "Rating")
                            ProfileStat("247", "Reviews")
                            ProfileStat("1.2k", "Patients")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                navController.navigate("editProfile")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2ECC71)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Profile")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            /* ---------------- ACCOUNT ---------------- */

            SectionTitle("Account")

            ProfileItem(
                icon = Icons.Filled.Person,
                title = "Personal Information",
                subtitle = "Name, email, phone, location",
                onClick = {
                    navController.navigate("doctorPersonalInformation")
                }
            )

            ProfileItem(
                icon = Icons.Filled.Info,
                title = "Professional Details",
                subtitle = "Specialization, experience, license",
                onClick = {
                    // Navigate - doctor_id will be retrieved from session in the screen
                    navController.navigate("doctorProfessionalDetails")
                }
            )

            /* ---------------- SETTINGS ---------------- */

            SectionTitle("Settings")

            ProfileItem(
                icon = Icons.Filled.Settings,
                title = "Settings & Preferences",
                subtitle = "Notifications, privacy, security",
                onClick = {
                    navController.navigate("doctorSettings")
                }
            )

            /* ---------------- SYNC ---------------- */

            SectionTitle("Sync Information")

            ProfileItem(
                icon = Icons.Filled.Refresh,
                title = "Sync Status",
                subtitle = "Synced",
                subtitleColor = Color(0xFF2ECC71),
                onClick = {
                    navController.navigate("doctorSync") // ✅ FIX
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            /* ---------------- LOGOUT ---------------- */

            Text(
                text = "Logout",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogout() }
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/* ---------------- API FUNCTION ---------------- */

private fun loadDoctorProfile(
    doctorId: Int,
    client: OkHttpClient,
    scope: kotlinx.coroutines.CoroutineScope,
    onResult: (Map<String, String>) -> Unit
) {
    val request = Request.Builder()
        .url("http://10.26.77.190/vaidyacare/api/get_doctor_profile.php?doctor_id=$doctorId")
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
                            "specialization" to data.optString("specialization", "General Medicine"),
                            "initials" to data.optString("initials", "DR"),
                            "profile_image" to data.optString("profile_image", "")
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

/* ---------------- COMPONENTS ---------------- */

@Composable
fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold)
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun ProfileItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    subtitleColor: Color = Color.Gray,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F8F1)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color(0xFF2ECC71)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(subtitle, fontSize = 12.sp, color = subtitleColor)
            }

            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}
