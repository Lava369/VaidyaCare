@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vaidyacare.utils.UserSession
import com.example.vaidyacare.network.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PersonalInformationScreen(
    navController: NavController,
    onBack: () -> Unit = { navController.popBackStack() }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Load data from UserSession
    var userName by remember { mutableStateOf(UserSession.getName(context)) }
    var userEmail by remember { mutableStateOf(UserSession.getEmail(context)) }
    var userMobile by remember { mutableStateOf(UserSession.getMobile(context)) }
    var patientId by remember { mutableStateOf(UserSession.getPatientId(context)) }
    
    // Profile data
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    
    // Load profile data from API
    LaunchedEffect(Unit) {
        val email = UserSession.getEmail(context)
        if (email.isNotEmpty()) {
            try {
                val response = RetrofitClient.api.getProfile(email)
                if (response.success && response.data != null) {
                    val data = response.data
                    userName = data.full_name.takeIf { it.isNotEmpty() } ?: userName
                    userEmail = data.email.takeIf { it.isNotEmpty() } ?: userEmail
                    userMobile = data.mobile.takeIf { it.isNotEmpty() } ?: userMobile
                    
                    // Format DOB
                    dob = if (!data.dob.isNullOrEmpty()) {
                        try {
                            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                            val date = inputFormat.parse(data.dob)
                            date?.let { outputFormat.format(it) } ?: data.dob
                        } catch (e: Exception) {
                            data.dob
                        }
                    } else {
                        "Not set"
                    }
                    
                    gender = data.gender ?: "Not set"
                    bloodGroup = data.blood_group ?: "Not set"
                    
                    // Format address
                    val addressParts = listOfNotNull(
                        data.address,
                        data.city,
                        data.state,
                        data.pin
                    ).filter { it.isNotEmpty() }
                    address = if (addressParts.isNotEmpty()) {
                        addressParts.joinToString(", ")
                    } else {
                        "Not set"
                    }
                    
                    // Format patient ID
                    patientId = data.patient_id?.takeIf { it.isNotEmpty() } 
                        ?: "VD${String.format("%05d", UserSession.getUserId(context))}"
                }
            } catch (e: Exception) {
                android.util.Log.e("PersonalInformation", "Error loading profile: ${e.message}")
                // Format patient ID from user_id if not available
                if (patientId.isEmpty()) {
                    val userId = UserSession.getUserId(context)
                    if (userId > 0) {
                        patientId = "VD${String.format("%05d", userId)}"
                    }
                }
            } finally {
                loading = false
            }
        } else {
            loading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personal Information") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF2F4F7)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            /* -----------------------------------------------------
             *                 PROFILE DETAILS CARD
             * ----------------------------------------------------- */
            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text(
                            "Profile Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        /* -------------------- PROFILE IMAGE + NAME -------------------- */
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE6ECF5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                if (loading) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                                } else {
                                    Text(
                                        userName.ifEmpty { "User" }, 
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Patient ID: #${patientId.ifEmpty { "N/A" }}",
                                        color = Color.Gray,
                                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        /* -------------------- EMAIL -------------------- */
                        InfoItem(
                            icon = Icons.Default.Email,
                            label = "Email",
                            value = if (loading) "Loading..." else userEmail.ifEmpty { "Not set" }
                        )

                        /* -------------------- PHONE -------------------- */
                        InfoItem(
                            icon = Icons.Default.Phone,
                            label = "Phone",
                            value = if (loading) "Loading..." else {
                                if (userMobile.isNotEmpty()) {
                                    "+91 $userMobile"
                                } else {
                                    "Not set"
                                }
                            }
                        )

                        /* -------------------- DOB -------------------- */
                        InfoItem(
                            icon = Icons.Default.DateRange,
                            label = "Date of Birth",
                            value = if (loading) "Loading..." else dob
                        )

                        /* -------------------- GENDER + BLOOD GROUP -------------------- */
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Gender", fontWeight = FontWeight.SemiBold)
                                Text(
                                    if (loading) "Loading..." else gender, 
                                    color = Color.DarkGray
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Blood Group", fontWeight = FontWeight.SemiBold)
                                Text(
                                    if (loading) "Loading..." else bloodGroup, 
                                    color = Color.DarkGray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        /* -------------------- ADDRESS -------------------- */
                        InfoItem(
                            icon = Icons.Default.LocationOn,
                            label = "Address",
                            value = if (loading) "Loading..." else address
                        )
                    }
                }
            }

            /* -----------------------------------------------------
             *                 EMERGENCY CONTACT CARD
             * ----------------------------------------------------- */
            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text(
                            "Emergency Contact",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        InfoRow(label = "Name", value = "Priya Kumar")
                        InfoRow(label = "Relationship", value = "Spouse")
                        InfoRow(label = "Phone", value = "+91 98765 43211")
                    }
                }
            }
        }
    }
}

/* -----------------------------------------------------
 *       REUSABLE INFO ITEM (WITH ICON)
 * ----------------------------------------------------- */
@Composable
fun InfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF4A6FFF))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(value, color = Color.DarkGray)
    }
}

/* -----------------------------------------------------
 *       REUSABLE TEXT ROW (NO ICON)
 * ----------------------------------------------------- */
@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Text(value, color = Color.DarkGray)
    }
}