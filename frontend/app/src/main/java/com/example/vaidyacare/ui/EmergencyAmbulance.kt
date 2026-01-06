package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyAmbulanceScreen(
    navController: NavController,   // ✅ Navigation enabled
    onBackClick: () -> Unit = {}
) {
    var contactNumber by remember { mutableStateOf("+91 98765 43210") }
    var patientName by remember { mutableStateOf("") }
    var emergencyDetails by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Emergency Ambulance",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE53935)
                )
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item { EmergencyModeCard() }
            item { CurrentLocationCard() }

            item {
                EmergencyContactCard(
                    contactNumber = contactNumber,
                    onContactChange = { contactNumber = it },
                    patientName = patientName,
                    onPatientChange = { patientName = it }
                )
            }

            item {
                EmergencyDetailsCard(
                    details = emergencyDetails,
                    onDetailsChange = { emergencyDetails = it }
                )
            }

            /* 🚨 MAIN BUTTON — Navigate to RequestConfirmed */
            item {
                Button(
                    onClick = { navController.navigate("requestConfirmed") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text(
                        "Request Emergency Ambulance Now",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Critical emergency?", fontSize = 13.sp, color = Color.Gray)
                    Text(
                        "Call 108 (Emergency Hotline)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE53935)
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

/* ------------------------------------------------------------------ */
/* COMPONENTS */
/* ------------------------------------------------------------------ */

@Composable
fun EmergencyModeCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Emergency Mode Active",
                color = Color(0xFFD32F2F),
                fontWeight = FontWeight.Bold
            )
            Text(
                "Nearest available ambulance will be dispatched to your current location.",
                color = Color.Gray,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun CurrentLocationCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text("Your Current Location", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Current Location", fontWeight = FontWeight.Medium)
                Spacer(Modifier.weight(1f))
                Text(
                    "Change",
                    color = Color(0xFF1E88E5),
                    modifier = Modifier.clickable { }
                )
            }
        }
    }
}

@Composable
fun EmergencyContactCard(
    contactNumber: String,
    onContactChange: (String) -> Unit,
    patientName: String,
    onPatientChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text("Emergency Contact", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = contactNumber,
                onValueChange = onContactChange,
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                label = { Text("Contact Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = patientName,
                onValueChange = onPatientChange,
                label = { Text("Patient Name (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

@Composable
fun EmergencyDetailsCard(details: String, onDetailsChange: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text("Emergency Details", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = details,
                onValueChange = onDetailsChange,
                label = { Text("Describe the emergency situation") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        }
    }
}
