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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ---------------- DATA MODEL ---------------- */

data class ScheduleAppointment(
    val time: String,
    val patientName: String,
    val consultationType: String
)

/* ---------------- MAIN SCREEN ---------------- */

@Composable
fun ScheduleScreen(
    onBack: () -> Unit = {}
) {
    var selectedDate by remember { mutableIntStateOf(1) }
    var currentMonth by remember { mutableIntStateOf(10) } // November (0-based)
    var currentYear by remember { mutableIntStateOf(2025) }

    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    val daysInMonth = listOf(
        31,
        if (currentYear % 4 == 0) 29 else 28,
        31, 30, 31, 30,
        31, 31, 30, 31, 30, 31
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            /* -------- MONTH HEADER -------- */

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = {
                    if (currentMonth == 0) {
                        currentMonth = 11
                        currentYear--
                    } else currentMonth--
                    selectedDate = 1
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.DateRange, null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${months[currentMonth]} $currentYear",
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = {
                    if (currentMonth == 11) {
                        currentMonth = 0
                        currentYear++
                    } else currentMonth++
                    selectedDate = 1
                }) {
                    Icon(Icons.Filled.ArrowForward, null)
                }
            }

            Spacer(Modifier.height(12.dp))

            /* -------- WEEK DAYS -------- */

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach {
                    Text(it, fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(Modifier.height(8.dp))

            /* -------- CALENDAR GRID -------- */

            (1..daysInMonth[currentMonth]).toList()
                .chunked(7)
                .forEach { week ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        week.forEach { day ->
                            ScheduleDayItem(
                                day = day,
                                selected = day == selectedDate,
                                onClick = { selectedDate = day }
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }

            Spacer(Modifier.height(24.dp))

            /* -------- UPCOMING -------- */

            Text("Upcoming This Week", fontWeight = FontWeight.Bold)

            ScheduleDayBlock(
                day = "Wednesday, Jan 15",
                appointments = listOf(
                    ScheduleAppointment("09:00 AM", "Rajesh Kumar", "video"),
                    ScheduleAppointment("10:30 AM", "Priya Sharma", "phone"),
                    ScheduleAppointment("02:00 PM", "Amit Patel", "video")
                )
            )

            ScheduleDayBlock(
                day = "Thursday, Jan 16",
                appointments = listOf(
                    ScheduleAppointment("09:30 AM", "Sneha Reddy", "video"),
                    ScheduleAppointment("11:00 AM", "Vikram Singh", "in-person")
                )
            )

            ScheduleDayBlock(
                day = "Friday, Jan 17",
                appointments = listOf(
                    ScheduleAppointment("10:00 AM", "Anita Desai", "video"),
                    ScheduleAppointment("03:00 PM", "Rahul Mehta", "phone")
                )
            )
        }
    }
}

/* ---------------- DAY ITEM ---------------- */

@Composable
fun ScheduleDayItem(
    day: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(
                if (selected) MaterialTheme.colorScheme.primary
                else Color(0xFFF2F2F2)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            color = if (selected) Color.White else Color.Black
        )
    }
}

/* ---------------- DAY BLOCK ---------------- */

@Composable
fun ScheduleDayBlock(
    day: String,
    appointments: List<ScheduleAppointment>
) {
    Spacer(Modifier.height(16.dp))
    Text(day, fontSize = 13.sp, color = Color.Gray)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            appointments.forEach {
                ScheduleAppointmentRow(it)
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

/* ---------------- APPOINTMENT ROW ---------------- */

@Composable
fun ScheduleAppointmentRow(appointment: ScheduleAppointment) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        Text(
            text = appointment.time,
            modifier = Modifier.width(70.dp),
            fontSize = 13.sp
        )

        Text(
            text = appointment.patientName,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Medium
        )

        ScheduleTypeChip(appointment.consultationType)
    }
}

/* ---------------- TYPE CHIP ---------------- */

@Composable
fun ScheduleTypeChip(type: String) {
    val color = when (type) {
        "video" -> Color(0xFFE3F2FD)
        "phone" -> Color(0xFFE8F5E9)
        else -> Color(0xFFF3E5F5)
    }

    Text(
        text = type,
        fontSize = 11.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
