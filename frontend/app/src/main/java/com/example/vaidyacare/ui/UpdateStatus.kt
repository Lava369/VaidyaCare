@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ---------------- STATUS TYPE ---------------- */

enum class DoctorStatus {
    AVAILABLE, BUSY, OFFLINE
}

/* ---------------- MAIN SCREEN ---------------- */

@Composable
fun UpdateStatusScreen(
    onBack: () -> Unit = {},
    onSave: (DoctorStatus, String, Boolean) -> Unit = { _, _, _ -> }
) {

    var selectedStatus by remember { mutableStateOf(DoctorStatus.AVAILABLE) }
    var statusMessage by remember { mutableStateOf("") }
    var autoReplyEnabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Update Status", fontWeight = FontWeight.Bold)
                        Text(
                            "Manage your availability",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
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
                .padding(16.dp)
        ) {

            /* -------- CURRENT STATUS -------- */

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF9C4DFF)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Current Status", color = Color.White, fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.Green)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            selectedStatus.name.lowercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("Select Status", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            StatusItem(
                title = "Available",
                subtitle = "Ready to accept consultations",
                selected = selectedStatus == DoctorStatus.AVAILABLE
            ) { selectedStatus = DoctorStatus.AVAILABLE }

            StatusItem(
                title = "Busy",
                subtitle = "In consultation or occupied",
                selected = selectedStatus == DoctorStatus.BUSY
            ) { selectedStatus = DoctorStatus.BUSY }

            StatusItem(
                title = "Offline",
                subtitle = "Not accepting consultations",
                selected = selectedStatus == DoctorStatus.OFFLINE
            ) { selectedStatus = DoctorStatus.OFFLINE }

            Spacer(Modifier.height(16.dp))

            Text("Custom Status Message (Optional)", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(6.dp))

            OutlinedTextField(
                value = statusMessage,
                onValueChange = { statusMessage = it },
                placeholder = {
                    Text("e.g., Back at 3 PM, In surgery, etc.")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Auto-Reply Messages", fontWeight = FontWeight.Medium)
                    Text(
                        "Automatically respond to patient messages\nwhen you're unavailable",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Switch(
                    checked = autoReplyEnabled,
                    onCheckedChange = { autoReplyEnabled = it }
                )
            }

            Spacer(Modifier.height(16.dp))

            Text("Quick Presets", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(10.dp))

            FlowRow(
                maxItemsInEachRow = 2,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PresetChip("In Consultation") { statusMessage = "In Consultation" }
                PresetChip("Lunch Break") { statusMessage = "On Lunch Break" }
                PresetChip("Emergency") { statusMessage = "Handling Emergency" }
                PresetChip("End of Day") { statusMessage = "Available Tomorrow" }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    onSave(selectedStatus, statusMessage, autoReplyEnabled)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9C4DFF)
                )
            ) {
                Text("Save Status", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/* ---------------- STATUS ITEM ---------------- */

@Composable
fun StatusItem(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFFE9F7EF) else Color.White
        ),
        border = if (selected)
            BorderStroke(1.dp, Color(0xFF2ECC71))
        else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }

            if (selected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF2ECC71)
                )
            }
        }
    }

    Spacer(Modifier.height(10.dp))
}

/* ---------------- PRESET CHIP ---------------- */

@Composable
fun PresetChip(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF4F4F4)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            fontSize = 13.sp
        )
    }
}
