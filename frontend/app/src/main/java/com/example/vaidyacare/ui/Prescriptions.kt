package com.example.vaidyacare.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

/* ---------------- DATA MODEL ---------------- */

data class Prescription(
    val id: String,
    val name: String,
    val dosage: String,
    val frequency: String,
    val doctor: String,
    val date: String,
    val refills: Int,
    val isActive: Boolean
)

/* ---------------- MAIN SCREEN ---------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionScreen(
    prescriptions: List<Prescription> = samplePrescriptions(),
    onBack: () -> Unit = {}
) {
    val active = remember(prescriptions) { prescriptions.filter { it.isActive } }
    val past = remember(prescriptions) { prescriptions.filter { !it.isActive } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Prescriptions") },
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

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item { MedicationReminderCard() }

            item { PrescriptionSectionTitle("Active Prescriptions") }

            items(active) { pres ->
                PrescriptionCard(pres)
            }

            item { PrescriptionSectionTitle("Past Prescriptions") }

            items(past) { pres ->
                PastPrescriptionCard(pres)
            }

            item { MedicationTipsCard() }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

/* ---------------- SECTION TITLE (RENAMED) ---------------- */

@Composable
private fun PrescriptionSectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

/* ---------------- REMINDER CARD ---------------- */

@Composable
private fun MedicationReminderCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F0FF))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Medication Reminders", fontWeight = FontWeight.SemiBold)
            Text(
                "Set up reminders to never miss your medications",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8648FF)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Set Reminders", color = Color.White)
            }
        }
    }
}

/* ---------------- ACTIVE PRESCRIPTION CARD ---------------- */

@Composable
private fun PrescriptionCard(pres: Prescription) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF2E9FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💊", fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(pres.name, fontWeight = FontWeight.SemiBold)
                    Text(
                        "${pres.dosage} • ${pres.frequency}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row {
                        Text("Dr. ${pres.doctor}", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(pres.date, fontSize = 12.sp, color = Color.Gray)
                    }
                }

                RefillBadge(pres.refills)
                Spacer(modifier = Modifier.width(6.dp))
                Text("⬇", fontSize = 18.sp)
            }

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8648FF)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Request Refill", color = Color.White)
            }
        }
    }
}

/* ---------------- REFILL BADGE ---------------- */

@Composable
private fun RefillBadge(refills: Int) {
    Box(
        modifier = Modifier
            .background(Color(0xFFE9FFF0), RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            "$refills refills",
            color = Color(0xFF2EB97A),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/* ---------------- PAST PRESCRIPTION ---------------- */

@Composable
private fun PastPrescriptionCard(pres: Prescription) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF1F1F1)),
                contentAlignment = Alignment.Center
            ) {
                Text("💊", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(pres.name, fontWeight = FontWeight.SemiBold)
                Text(
                    "${pres.dosage} • ${pres.frequency}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row {
                    Text("Dr. ${pres.doctor}", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(pres.date, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

/* ---------------- TIPS CARD ---------------- */

@Composable
private fun MedicationTipsCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Medication Tips", fontWeight = FontWeight.SemiBold)
            Text("• Take medications at the same time each day", fontSize = 13.sp, color = Color.Gray)
            Text("• Never share your prescription medications", fontSize = 13.sp, color = Color.Gray)
            Text("• Store medications in a cool, dry place", fontSize = 13.sp, color = Color.Gray)
            Text("• Check expiration dates regularly", fontSize = 13.sp, color = Color.Gray)
        }
    }
}

/* ---------------- SAMPLE DATA ---------------- */

private fun samplePrescriptions(): List<Prescription> = listOf(
    Prescription("p1", "Metformin", "500mg", "Twice daily", "Sharma", "May 10, 2023", 2, true),
    Prescription("p2", "Amlodipine", "5mg", "Once daily", "Patel", "May 8, 2023", 1, true),
    Prescription("p3", "Amoxicillin", "500mg", "Three times daily", "Sharma", "April 15, 2023", 0, false)
)

/* ---------------- PREVIEWS ---------------- */

@Preview(showBackground = true)
@Composable
private fun PreviewPrescriptionLight() {
    MaterialTheme {
        PrescriptionScreen()
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewPrescriptionDark() {
    MaterialTheme {
        PrescriptionScreen()
    }
}
