@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/* ---------------------------------------------------------
 * DATA CLASS
 * --------------------------------------------------------- */

data class Appointment(
    val doctorName: String,
    val specialization: String,
    val date: String,
    val time: String,
    val mode: String,
    val status: String = "Completed"
)

/* ---------------------------------------------------------
 * MAIN SCREEN
 * --------------------------------------------------------- */

@Composable
fun AppointmentsHistoryScreen(
    navController: NavController,
    onBack: () -> Unit = { navController.popBackStack() }
) {
    var selectedTab by remember { mutableStateOf(1) }  // 0 = Upcoming, 1 = Past

    val upcoming = listOf(
        Appointment("Dr. Sharma", "Cardiologist", "May 25, 2023", "10:00 AM", "Video Consultation", "Confirmed"),
        Appointment("Dr. Patel", "General Physician", "May 27, 2023", "2:30 PM", "VaidyaCare Main Clinic", "Confirmed")
    )

    val past = listOf(
        Appointment("Dr. Sharma", "Cardiologist", "May 10, 2023", "10:00 AM", "Video Consultation"),
        Appointment("Dr. Gupta", "Dermatologist", "April 28, 2023", "3:00 PM", "VaidyaCare North Branch"),
        Appointment("Dr. Patel", "General Physician", "April 15, 2023", "11:30 AM", "Video Consultation"),
        Appointment("Dr. Sharma", "Cardiologist", "March 20, 2023", "9:00 AM", "VaidyaCare Main Clinic")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appointments History") },
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
                .background(Color(0xFFF4F6FA)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            /* ------------------- Tabs ------------------- */
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFFE7EBF5)),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    TabButton(
                        text = "Upcoming ( ${upcoming.size} )",
                        selected = selectedTab == 0,
                        modifier = Modifier.weight(1f)
                    ) { selectedTab = 0 }

                    TabButton(
                        text = "Past ( ${past.size} )",
                        selected = selectedTab == 1,
                        modifier = Modifier.weight(1f)
                    ) { selectedTab = 1 }
                }
            }

            /* ------------------- Appointments List ------------------- */

            val activeList = if (selectedTab == 0) upcoming else past

            items(activeList.size) { index ->
                AppointmentCard(
                    appointment = activeList[index],
                    isUpcoming = selectedTab == 0
                )
            }

            /* ------------------- Statistics for Past ------------------- */

            if (selectedTab == 1) {
                item {
                    StatsCard(total = upcoming.size + past.size, completed = past.size)
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}

/* ---------------------------------------------------------
 * TAB BUTTON
 * --------------------------------------------------------- */

@Composable
fun TabButton(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {

    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(
                if (selected) Color(0xFF2F6BFF) else Color.Transparent,
                RoundedCornerShape(22.dp)
            )
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color.Gray,
            fontWeight = FontWeight.Medium
        )
    }
}

/* ---------------------------------------------------------
 * APPOINTMENT CARD UI
 * --------------------------------------------------------- */

@Composable
fun AppointmentCard(appointment: Appointment, isUpcoming: Boolean) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            /* -------- Header: Doctor Info + Status -------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE5ECF7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(appointment.doctorName, fontWeight = FontWeight.Bold)
                        Text(appointment.specialization, color = Color.Gray, fontSize = 13.sp)
                    }
                }

                // Status badge
                if (isUpcoming) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFDDF7E7))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            appointment.status,
                            color = Color(0xFF1A9A55),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE8E8E8))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Completed", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            /* -------- Date -------- */
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text(appointment.date)
            }

            Spacer(modifier = Modifier.height(6.dp))

            /* -------- Time (using emoji to avoid icon issues) -------- */
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⏱", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(appointment.time)
            }

            Spacer(modifier = Modifier.height(6.dp))

            /* -------- Mode / Location -------- */
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text(appointment.mode)
            }

            Spacer(modifier = Modifier.height(16.dp))

            /* -------- Action Buttons -------- */
            if (isUpcoming) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F6BFF)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Join Call", color = Color.White)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    TextButton(onClick = {}) { Text("Reschedule", color = Color.Gray) }
                }

            } else {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F6BFF)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Book Again", color = Color.White)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    TextButton(onClick = {}) {
                        Text("View Prescription", color = Color(0xFF2F6BFF))
                    }
                }
            }
        }
    }
}

/* ---------------------------------------------------------
 * STATISTICS CARD
 * --------------------------------------------------------- */

@Composable
fun StatsCard(total: Int, completed: Int) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F0FF))
    ) {

        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            StatItem(total, "Total Appointments")
            StatItem(completed, "Completed")
        }
    }
}

@Composable
fun StatItem(count: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Color.Gray, fontSize = 12.sp)
    }
}