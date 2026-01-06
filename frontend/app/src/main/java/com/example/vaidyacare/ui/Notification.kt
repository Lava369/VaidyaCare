package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(onBack: () -> Unit = {}) {

    var appointmentReminders by remember { mutableStateOf(true) }
    var reportNotifications by remember { mutableStateOf(true) }
    var medicationReminders by remember { mutableStateOf(true) }
    var healthTips by remember { mutableStateOf(false) }

    val notifications = listOf(
        NotificationItem(
            title = "Upcoming Appointment",
            message = "Video consultation with Dr. Sharma tomorrow at 10:00 AM",
            time = "2 hours ago",
            icon = "📅"
        ),
        NotificationItem(
            title = "New Report Available",
            message = "Your blood test results are ready to view",
            time = "5 hours ago",
            icon = "📄"
        ),
        NotificationItem(
            title = "Medication Reminder",
            message = "Time to take your Metformin (500mg)",
            time = "8 hours ago",
            icon = "💊"
        ),
        NotificationItem(
            title = "New Video Explanation",
            message = "Dr. Sharma uploaded a video explaining your test results",
            time = "Yesterday",
            icon = "🎥"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    Text(
                        text = "←",
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { onBack() },
                        fontSize = 22.sp
                    )
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .background(Color(0xFFF4F4F4))
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Recent Notifications", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Mark all as read",
                        color = Color(0xFF3B5AFB),
                        modifier = Modifier.clickable { },
                        fontSize = 14.sp
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        notifications.forEachIndexed { index, notif ->
                            NotificationRow(notif)
                            if (index != notifications.lastIndex) {
                                Spacer(Modifier.height(18.dp))
                            }
                        }
                    }
                }
            }

            item {
                NotificationPreferencesCard(
                    appointmentReminders = appointmentReminders,
                    onAppointmentToggle = { appointmentReminders = it },
                    reportNotifications = reportNotifications,
                    onReportToggle = { reportNotifications = it },
                    medicationReminders = medicationReminders,
                    onMedicationToggle = { medicationReminders = it },
                    healthTips = healthTips,
                    onHealthTipsToggle = { healthTips = it }
                )
            }
        }
    }
}

@Composable
fun NotificationRow(item: NotificationItem) {
    Row(verticalAlignment = Alignment.Top) {

        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFFE8EFFE), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = item.icon, fontSize = 20.sp)
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(item.message, fontSize = 14.sp, color = Color.Gray)
            Text(item.time, fontSize = 12.sp, color = Color.Gray)
        }

        Box(
            modifier = Modifier
                .size(10.dp)
                .background(Color(0xFF3B5AFB), CircleShape)
        )
    }
}

@Composable
fun NotificationPreferencesCard(
    appointmentReminders: Boolean,
    onAppointmentToggle: (Boolean) -> Unit,
    reportNotifications: Boolean,
    onReportToggle: (Boolean) -> Unit,
    medicationReminders: Boolean,
    onMedicationToggle: (Boolean) -> Unit,
    healthTips: Boolean,
    onHealthTipsToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text("Notification Preferences", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(12.dp))

            PreferenceToggle(
                "Appointment Reminders",
                "Get notified before appointments",
                appointmentReminders,
                onAppointmentToggle
            )

            PreferenceToggle(
                "Report Notifications",
                "Alert when new reports are available",
                reportNotifications,
                onReportToggle
            )

            PreferenceToggle(
                "Medication Reminders",
                "Daily medication reminders",
                medicationReminders,
                onMedicationToggle
            )

            PreferenceToggle(
                "Health Tips",
                "Daily health tips and advice",
                healthTips,
                onHealthTipsToggle
            )
        }
    }
}

@Composable
fun PreferenceToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(subtitle, fontSize = 13.sp, color = Color.Gray)
        }

        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

data class NotificationItem(
    val title: String,
    val message: String,
    val time: String,
    val icon: String     // NOW A STRING (emoji) → NO ERROR POSSIBLE
)
