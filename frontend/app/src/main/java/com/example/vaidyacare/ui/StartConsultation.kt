@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vaidyacare.R

/* ---------------- DATA MODEL ---------------- */

data class Patient(
    val name: String,
    val age: String,
    val gender: String,
    val problem: String,
    val lastVisit: String
)

/* ---------------- MAIN SCREEN ---------------- */

@Composable
fun StartConsultationScreen(
    onBack: () -> Unit = {},
    onStart: () -> Unit = {}
) {

    var selectedType by remember { mutableStateOf<String?>(null) }

    val patients = listOf(
        Patient("Rajesh Kumar", "45", "Male", "Diabetes follow-up", "2 days ago"),
        Patient("Priya Sharma", "32", "Female", "Skin consultation", "1 week ago"),
        Patient("Amit Patel", "28", "Male", "Fever and cold", "3 days ago"),
        Patient("Sneha Reddy", "38", "Female", "Blood pressure", "5 days ago"),
        Patient("Vikram Singh", "52", "Male", "Chest pain", "1 week ago")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Start Consultation", fontWeight = FontWeight.Bold)
                        Text(
                            "Select patient and consultation type",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = onStart,
                enabled = selectedType != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Select Consultation Type")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            /* ---------- CONSULTATION TYPE ---------- */

            Text("Consultation Type", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Box(modifier = Modifier.weight(1f)) {
                    ConsultationTypeCard(
                        title = "Video Call",
                        icon = R.drawable.ic_video,
                        selected = selectedType == "Video",
                        onClick = { selectedType = "Video" }
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    ConsultationTypeCard(
                        title = "Phone Call",
                        icon = R.drawable.ic_phone,
                        selected = selectedType == "Phone",
                        onClick = { selectedType = "Phone" }
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    ConsultationTypeCard(
                        title = "Message",
                        icon = R.drawable.ic_chat,
                        selected = selectedType == "Message",
                        onClick = { selectedType = "Message" }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            /* ---------- PATIENT LIST ---------- */

            Text("Select Patient", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(16.dp))

            patients.forEach {
                PatientCard(it)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

/* ---------------- CONSULTATION CARD ---------------- */

@Composable
fun ConsultationTypeCard(
    title: String,
    icon: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bgColor =
        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title
            )
            Spacer(Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Medium)
        }
    }
}

/* ---------------- PATIENT CARD ---------------- */

@Composable
fun PatientCard(patient: Patient) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(patient.name, fontWeight = FontWeight.Bold)
                Text(patient.lastVisit, fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(Modifier.height(4.dp))

            Text(
                "${patient.age} • ${patient.gender}",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(6.dp))

            Text(patient.problem)
        }
    }
}
