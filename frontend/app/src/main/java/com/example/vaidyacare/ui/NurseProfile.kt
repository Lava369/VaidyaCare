package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NurseProfileScreen(
    navController: NavController,
    nurse: NurseProfile
) {

    val gradient = Brush.verticalGradient(
        listOf(Color(0xFFB24A00), Color(0xFFFF7A00))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(Color.Transparent)
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
        ) {

            // HEADER
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(gradient)
                        .padding(16.dp)
                ) {
                    Text(
                        "Nurse Profile",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            // PROFILE CARD
            item {
                Column(
                    modifier = Modifier
                        .offset(y = (-40).dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(16.dp)
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFEFEFEF))
                        )

                        Spacer(Modifier.width(16.dp))

                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(nurse.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Spacer(Modifier.width(6.dp))
                                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF00C853))
                            }

                            Text("Clinical Nurse", fontSize = 14.sp, color = Color.Gray)

                            Spacer(Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, null, tint = Color(0xFFFF9800))
                                Text("${nurse.rating} (${nurse.reviews} reviews)", fontSize = 13.sp)
                                Spacer(Modifier.width(16.dp))
                                Icon(Icons.Default.LocationOn, null, tint = Color.Gray)
                                Text(nurse.distance, fontSize = 13.sp, color = Color.Gray)
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Experienced clinical nurse with 8+ years in critical care.",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
            }

            // CREDENTIALS
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Verified Credentials", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))

                    CredentialItem("License Status", "Verified")
                    CredentialItem("Experience", nurse.experience)
                    CredentialItem("Qualification", nurse.qualification)
                    CredentialItem("Verified By", "TNNMC")
                }
            }

            // SPECIALTIES
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Specialties", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))

                    nurse.skills.forEach { Chip(it) }
                }
            }

            // PRICE SECTION
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Pricing", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))

                    PriceItem("Home Visit Charge", nurse.price)
                    PriceItem("Consultation Fee", "₹300")
                    PriceItem("Travel Charge", "₹50")
                }
            }

            // BOOK BUTTON
            item {
                Button(
                    onClick = {
                        navController.navigate("homeService")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFFFF7A00))
                ) {
                    Text("Book This Nurse", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}

/* ---------- Reusable Components ---------- */

@Composable
fun CredentialItem(title: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF00C853))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(value, color = Color.Gray)
        }
    }
}

@Composable
fun Chip(text: String) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEAF0FF))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, fontSize = 13.sp, color = Color(0xFF325DFF))
    }
}

@Composable
fun PriceItem(name: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(name, fontSize = 14.sp)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
