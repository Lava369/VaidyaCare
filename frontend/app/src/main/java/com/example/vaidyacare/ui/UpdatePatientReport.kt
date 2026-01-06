@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UpdatePatientReportScreen(
    onBack: () -> Unit = {},
    onUpload: () -> Unit = {}
) {

    var selectedPatient by remember { mutableStateOf("") }
    var selectedReportType by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val isFormValid = selectedPatient.isNotEmpty() && selectedReportType.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Upload Patient Report", fontWeight = FontWeight.Bold)
                        Text(
                            "Add medical documents and reports",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Back")
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

            /* ---------- Select Patient ---------- */

            Text("Select Patient *", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(6.dp))

            DropdownField(
                value = selectedPatient,
                placeholder = "Choose a patient...",
                options = listOf("Rajesh Kumar", "Anita Sharma", "Vikram Singh"),
                onSelected = { selectedPatient = it }
            )

            Spacer(Modifier.height(16.dp))

            /* ---------- Report Type ---------- */

            Text("Report Type *", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(6.dp))

            DropdownField(
                value = selectedReportType,
                placeholder = "Select report type...",
                options = listOf(
                    "Blood Test",
                    "X-Ray",
                    "MRI Scan",
                    "Prescription",
                    "Medical Summary"
                ),
                onSelected = { selectedReportType = it }
            )

            Spacer(Modifier.height(16.dp))

            /* ---------- Upload Files ---------- */

            Text("Upload Files *", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .border(
                        1.dp,
                        Color(0xFFDDDDDD),
                        RoundedCornerShape(12.dp)
                    )
                    .background(Color(0xFFF9F9F9))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        "Drag and drop files here",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(6.dp))
                    Text("or", color = Color.Gray, fontSize = 12.sp)

                    Spacer(Modifier.height(10.dp))

                    Button(onClick = { }) {
                        Text("Browse Files")
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Supported formats: PDF, JPG, PNG, DOC (Max 10MB per file)",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            /* ---------- Notes ---------- */

            Text("Additional Notes (Optional)", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(6.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = {
                    Text("Add any relevant notes about the report...")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(24.dp))

            /* ---------- Upload Button ---------- */

            Button(
                onClick = onUpload,
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Upload Report", fontSize = 16.sp)
            }
        }
    }
}

/* ---------------------------------------------------
   SIMPLE DROPDOWN (NO ICONS)
--------------------------------------------------- */

@Composable
fun DropdownField(
    value: String,
    placeholder: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        placeholder = { Text(placeholder) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true },
        shape = RoundedCornerShape(12.dp)
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        options.forEach { item ->
            DropdownMenuItem(
                text = { Text(item) },
                onClick = {
                    onSelected(item)
                    expanded = false
                }
            )
        }
    }
}
