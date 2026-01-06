package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BySpecialtyScreen(
    navController: NavController
) {
    val bgGradient = Brush.verticalGradient(
        listOf(Color(0xFFF5F9FF), Color.White)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Browse by Specialty",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(specialtyList.size) { index ->
                val item = specialtyList[index]

                SpecialtyCard(
                    icon = item.icon,
                    title = item.title,
                    count = item.count,
                    bg = item.bg,
                    onClick = {
                        // Navigate to specialty screen with specialization parameter
                        val specialization = when (item.title) {
                            "General Physician" -> "General Physician"
                            "Cardiologist" -> "Cardiologist"
                            "Dermatologist" -> "Dermatologist"
                            "Pediatrician" -> "Pediatrician"
                            "Orthopedic" -> "Orthopedic"
                            "Gynecologist" -> "Gynecologist"
                            "ENT Specialist" -> "ENT Specialist"
                            "Neurologist" -> "Neurologist"
                            "Psychiatrist" -> "Psychiatrist"
                            "Dentist" -> "Dentist"
                            else -> item.title
                        }
                        navController.navigate("specialty_doctors/${specialization}")
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(60.dp)) }
        }
    }
}

/* ---------------- DATA MODEL ---------------- */

data class Specialty(
    val icon: String,
    val title: String,
    val count: Int,
    val bg: Color
)

val specialtyList = listOf(
    Specialty("🩺", "General Physician", 45, Color(0xFFEFF5FF)),
    Specialty("❤️", "Cardiologist", 23, Color(0xFFFFF0F2)),
    Specialty("✨", "Dermatologist", 18, Color(0xFFF9EDFF)),
    Specialty("👶", "Pediatrician", 32, Color(0xFFE7FFF2)),
    Specialty("🦴", "Orthopedic", 15, Color(0xFFFFF5E7)),
    Specialty("🌸", "Gynecologist", 20, Color(0xFFFFF0F9)),
    Specialty("👂", "ENT Specialist", 12, Color(0xFFEFFFF8)),
    Specialty("🧠", "Neurologist", 10, Color(0xFFEFF0FF)),
    Specialty("🧘", "Psychiatrist", 14, Color(0xFFF6EDFF)),
    Specialty("🦷", "Dentist", 28, Color(0xFFEFFFFF))
)

/* ---------------- CARD UI ---------------- */

@Composable
fun SpecialtyCard(
    icon: String,
    title: String,
    count: Int,
    bg: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text(icon, fontSize = 30.sp)
                Spacer(Modifier.height(6.dp))
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text("$count doctors available", fontSize = 12.sp, color = Color.Gray)
            }

            Text("›", fontSize = 28.sp, color = Color.Gray)
        }
    }
}