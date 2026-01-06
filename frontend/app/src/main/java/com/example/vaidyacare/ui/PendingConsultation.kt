@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ---------------- DATA ---------------- */

data class PendingConsultation(
    val name: String,
    val waitingTime: String,
    val callType: String,   // Video Call / Phone Call
    val priority: String    // High / Medium / Low
)

/* ---------------- SCREEN ---------------- */

@Composable
fun PendingConsultationScreen(
    onBack: () -> Unit = {}
) {

    val consultations = listOf(
        PendingConsultation("Arun Kumar", "12 min", "Video Call", "High"),
        PendingConsultation("Sita Devi", "8 min", "Video Call", "Medium"),
        PendingConsultation("Mohan Lal", "5 min", "Phone Call", "High"),
        PendingConsultation("Lakshmi Iyer", "15 min", "Video Call", "Low"),
        PendingConsultation("Ravi Shankar", "3 min", "Video Call", "High"),
        PendingConsultation("Geeta Patel", "20 min", "Phone Call", "Medium"),
        PendingConsultation("Sunil Reddy", "10 min", "Video Call", "Medium"),
        PendingConsultation("Nisha Gupta", "6 min", "Video Call", "High")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Pending Consultations", fontWeight = FontWeight.Bold)
                        Text(
                            "${consultations.size} patients waiting",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(consultations) { item ->
                PendingConsultationCard(item)
            }
        }
    }
}

/* ---------------- CARD ---------------- */

@Composable
fun PendingConsultationCard(item: PendingConsultation) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                PriorityChip(item.priority)
            }

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {

                Text(
                    "Waiting ${item.waitingTime}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.width(16.dp))

                // SAFE ICON (exists in all projects)
                Icon(
                    Icons.Default.Call,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    item.callType,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1BA548)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Start Consultation", color = Color.White)
            }
        }
    }
}

/* ---------------- PRIORITY CHIP ---------------- */

@Composable
fun PriorityChip(priority: String) {

    val (bg, text) = when (priority) {
        "High" -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
        "Medium" -> Color(0xFFFFF8E1) to Color(0xFFF9A825)
        else -> Color(0xFFE8F5E9) to Color(0xFF388E3C)
    }

    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(priority, fontSize = 12.sp, color = text, fontWeight = FontWeight.Medium)
    }
}
