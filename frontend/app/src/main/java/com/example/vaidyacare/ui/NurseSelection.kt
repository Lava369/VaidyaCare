package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.net.URLEncoder

data class NurseItem(
    val title: String,
    val description: String,
    val recommended: String,
    val iconEmoji: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NurseSelectionScreen(
    navController: NavController
) {

    val gradient = Brush.verticalGradient(
        listOf(Color(0xFFFF7A00), Color(0xFFFFA74D))
    )

    val nurseList = listOf(
        NurseItem("Wound Care / Injections / IV Fluids", "Clinical procedures requiring medical expertise", "Clinical Nurse", "💉"),
        NurseItem("Elderly Care / Bedridden Support", "Daily assistance and monitoring for seniors", "Home Care Nurse", "🖤"),
        NurseItem("Child Care", "Specialized pediatric nursing care", "Pediatric Nurse", "👶"),
        NurseItem("Post-Surgery Dressings", "Surgical wound care and recovery support", "Surgical Nurse", "🩹"),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(Color.Transparent)
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(gradient)
                        .padding(20.dp)
                ) {
                    Column(Modifier.align(Alignment.BottomStart)) {
                        Text("What do you need?", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("We'll recommend the right nurse specialist", fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(nurseList.size) { index ->

                val item = nurseList[index]

                NurseOptionCard(
                    item = item,
                    onClick = {

                        // FIX: Encode title to avoid navigation crash
                        val encoded = URLEncoder.encode(item.title, "UTF-8")

                        navController.navigate("selectNurse/$encoded")
                    }
                )
            }
        }
    }
}

@Composable
fun NurseOptionCard(
    item: NurseItem,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.iconEmoji, fontSize = 28.sp, modifier = Modifier.padding(end = 12.dp))

                Column {
                    Text(item.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(item.description, fontSize = 13.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, tint = Color(0xFFFF6B00), modifier = Modifier.size(18.dp))
                Text("  Recommended:  ${item.recommended}", color = Color(0xFFFF6B00), fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
