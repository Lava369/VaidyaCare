@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* -------------------------------------------------
   LOCAL MODEL (NO CONFLICT WITH PROJECT MODELS)
------------------------------------------------- */
data class TodayAppointment(
    val time: String,
    val patientName: String,
    val patientAge: String,
    val problem: String,
    val status: String // Upcoming | In Progress | Completed
)

/* ---------------- MAIN SCREEN ---------------- */

@Composable
fun TodaysAppointmentScreen(
    onBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }

    val appointments = remember {
        listOf(
            TodayAppointment("09:00 AM", "Rajesh Kumar", "45", "Diabetes follow-up", "Completed"),
            TodayAppointment("09:30 AM", "Priya Sharma", "32", "Cold consultation", "Completed"),
            TodayAppointment("10:00 AM", "Amit Patel", "28", "Fever and body aches", "In Progress"),
            TodayAppointment("10:30 AM", "Sneha Reddy", "38", "Blood pressure check", "Upcoming"),
            TodayAppointment("11:00 AM", "Vikram Singh", "52", "Chest pain evaluation", "Upcoming"),
            TodayAppointment("11:30 AM", "Anita Desai", "41", "Thyroid medication review", "Upcoming"),
            TodayAppointment("02:00 PM", "Rahul Mehta", "35", "Back pain consultation", "Upcoming"),
            TodayAppointment("02:30 PM", "Kavita Joshi", "29", "Pregnancy checkup", "Upcoming")
        )
    }

    val filteredAppointments = when (selectedTab) {
        1 -> appointments.filter {
            it.status == "Upcoming" || it.status == "In Progress"
        }
        2 -> appointments.filter { it.status == "Completed" }
        else -> appointments
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Today's Appointments")
                        Text(
                            "${appointments.size} appointments scheduled",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            /* ---------------- TABS ---------------- */

            TabRow(selectedTabIndex = selectedTab) {

                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("All (${appointments.size})") }
                )

                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Upcoming (${
                                appointments.count {
                                    it.status == "Upcoming" || it.status == "In Progress"
                                }
                            })"
                        )
                    }
                )

                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            "Completed (${appointments.count { it.status == "Completed" }})"
                        )
                    }
                )
            }

            /* ---------------- LIST ---------------- */

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF6F7F9))
                    .padding(12.dp)
            ) {
                items(filteredAppointments) { appointment ->
                    AppointmentCard(appointment)
                }
            }
        }
    }
}

/* ---------------- APPOINTMENT CARD ---------------- */

@Composable
fun AppointmentCard(appointment: TodayAppointment) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Text(
                    appointment.time,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(appointment.patientName, fontWeight = FontWeight.Bold)
                    Text(
                        "${appointment.patientAge} • ${appointment.problem}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                StatusChip(appointment.status)
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                TextButton(onClick = { }) {
                    Text("View")
                }

                Spacer(Modifier.width(8.dp))

                when (appointment.status) {

                    "Completed" -> {
                        OutlinedButton(onClick = { }) {
                            Text("View Notes", fontSize = 13.sp)
                        }
                    }

                    "In Progress" -> {
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF22C55E)
                            )
                        ) {
                            Text("Join", fontSize = 13.sp)
                        }
                    }

                    else -> {
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2563EB)
                            )
                        ) {
                            Text("Start", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

/* ---------------- STATUS CHIP ---------------- */

@Composable
fun StatusChip(status: String) {

    val (text, color) = when (status) {
        "Completed" -> "Completed" to Color(0xFF6B7280)
        "In Progress" -> "In Progress" to Color(0xFF22C55E)
        else -> "Upcoming" to Color(0xFF2563EB)
    }

    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, color = color, fontSize = 12.sp)
    }
}
