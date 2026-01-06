
package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vaidyacare.utils.UserSession
import com.example.vaidyacare.network.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    onBack: () -> Unit = { navController.popBackStack() }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Load data from UserSession
    var userName by remember { mutableStateOf(UserSession.getName(context)) }
    var userEmail by remember { mutableStateOf(UserSession.getEmail(context)) }
    var patientId by remember { mutableStateOf(UserSession.getPatientId(context)) }
    var loading by remember { mutableStateOf(false) }
    
    // Load profile data from API
    LaunchedEffect(Unit) {
        val email = UserSession.getEmail(context)
        if (email.isNotEmpty()) {
            loading = true
            try {
                val response = RetrofitClient.api.getProfile(email)
                if (response.success && response.data != null) {
                    userName = response.data.full_name.takeIf { it.isNotEmpty() } ?: userName
                    userEmail = response.data.email.takeIf { it.isNotEmpty() } ?: userEmail
                    // Format patient ID
                    patientId = response.data.patient_id?.takeIf { it.isNotEmpty() } 
                        ?: "VD${String.format("%05d", UserSession.getUserId(context))}"
                }
            } catch (e: Exception) {
                // Use session data if API fails
                android.util.Log.e("ProfileScreen", "Error loading profile: ${e.message}")
            } finally {
                loading = false
            }
        } else {
            // Format patient ID from user_id if not available
            if (patientId.isEmpty()) {
                val userId = UserSession.getUserId(context)
                if (userId > 0) {
                    patientId = "VD${String.format("%05d", userId)}"
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            /** ------------------ PROFILE CARD ------------------ */
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDDE7FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile Icon",
                                tint = Color(0xFF4C5CFF),
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            if (loading) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                            } else {
                                Text(
                                    userName.ifEmpty { "User" }, 
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    userEmail.ifEmpty { "No email" }, 
                                    fontSize = 13.sp, 
                                    color = Color.Gray
                                )
                                Text(
                                    "Patient ID: #${patientId.ifEmpty { "N/A" }}", 
                                    fontSize = 12.sp, 
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            item { Text("Profile Options", fontSize = 16.sp, color = Color.DarkGray) }


            /** ------------------ OPTION ITEMS ------------------ */

            item {
                ProfileOptionNavItem(
                    title = "Edit Profile",
                    icon = Icons.Default.Edit,
                    bgColor = Color(0xFFE8F0FF),
                    onClick = { navController.navigate("editProfile") }
                )
            }

            item {
                ProfileOptionNavItem(
                    title = "Personal Information",
                    icon = Icons.Default.Info,
                    bgColor = Color(0xFFF3E9FF),
                    onClick = { navController.navigate("personalInfo") }
                )
            }

            item {
                ProfileOptionNavItem(
                    title = "Family Health Manager",
                    icon = Icons.Default.Person,
                    bgColor = Color(0xFFE9F7FF),
                    onClick = { navController.navigate("familyManager") }
                )
            }

            item {
                ProfileOptionNavItem(
                    title = "Appointments History",
                    icon = Icons.Default.DateRange,
                    bgColor = Color(0xFFE6FFE7),
                    onClick = { navController.navigate("appointmentsHistory") }
                )
            }

            item {
                ProfileOptionNavItem(
                    title = "Health Records",
                    icon = Icons.Default.Info,
                    bgColor = Color(0xFFFFF2E0),
                    onClick = { navController.navigate("healthRecords") }
                )
            }

            item {
                ProfileOptionNavItem(
                    title = "My Prescriptions",
                    icon = Icons.AutoMirrored.Filled.List,
                    bgColor = Color(0xFFFFE8F0),
                    onClick = { navController.navigate("prescriptions") }
                )
            }

            item {
                ProfileOptionNavItem(
                    title = "Notifications",
                    icon = Icons.Default.Notifications,
                    bgColor = Color(0xFFFFEBEB),
                    onClick = { navController.navigate("notifications") }
                )
            }

            item {
                ProfileOptionNavItem(
                    title = "Help & Support",
                    icon = Icons.Default.Info,
                    bgColor = Color(0xFFE9F8FF),
                    onClick = { navController.navigate("help_and_support") }
                )
            }

            item {
                ProfileOptionNavItem(
                    title = "Sync Info",
                    icon = Icons.Default.Refresh,
                    bgColor = Color(0xFFEDEAFF),
                    onClick = { navController.navigate("syncInfo") }
                )
            }

            item {
                ProfileOptionNavItem(
                    title = "Settings",
                    icon = Icons.Default.Settings,
                    bgColor = Color(0xFFF5F5F5),
                    onClick = { navController.navigate("settings") }  // ⭐ Correct navigation
                )
            }

            /** ------------------ LOGOUT BUTTON ------------------ */
            item {
                Button(
                    onClick = {
                        navController.navigate("welcome") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color.White
                    )
                    Spacer(Modifier.width(10.dp))
                    Text("Log Out", color = Color.White)
                }
            }
        }
    }
}


/** ------------------ REUSABLE OPTION ROW ------------------ */

@Composable
fun ProfileOptionNavItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    bgColor: Color,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Icon Box
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = Color(0xFF4A6FFF),
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.width(16.dp))

        Text(title, fontSize = 15.sp, modifier = Modifier.weight(1f))

        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Go",
            tint = Color.Gray
        )
    }
}
