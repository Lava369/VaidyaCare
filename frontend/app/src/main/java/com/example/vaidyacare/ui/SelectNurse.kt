package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.net.URLEncoder

/* ---------------- MODEL ---------------- */

data class NurseProfile(
    val name: String,
    val qualification: String,
    val rating: Double,
    val reviews: Int,
    val experience: String,
    val skills: List<String>,
    val distance: String,
    val availability: String,
    val price: String
)

/* Dummy Data */
val nurseList = listOf(
    NurseProfile("Sarah Johnson", "B.Sc Nursing, M.Sc (Critical Care)", 4.9, 127, "8 yrs exp", listOf("IV Therapy", "Wound Dressing"), "2.3 km", "Available", "₹500"),
    NurseProfile("Anjali Sharma", "B.Sc Nursing, Diabetes Educator", 4.8, 142, "7 yrs exp", listOf("Diabetes Care", "Insulin Administration"), "1.8 km", "Available", "₹550")
)

/* ---------------- SCREEN ---------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectNurseScreen(
    navController: NavController,
    selectedCategory: String,
    onNurseSelected: (NurseProfile) -> Unit = {}
) {

    val gradient = Brush.verticalGradient(listOf(Color(0xFFFF7A00), Color(0xFFFFA74D)))

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
                        Text("Select Nurse", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Category: $selectedCategory", fontSize = 15.sp, color = Color.White.copy(alpha = 0.9f))
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            item {
                Text("${nurseList.size} verified nurses found",
                    modifier = Modifier.padding(start = 16.dp),
                    fontSize = 13.sp, color = Color.Gray)
                Spacer(Modifier.height(10.dp))
            }

            items(nurseList) { nurse ->
                NurseCard(nurse) {

                    // ✅ Encode nurse name to avoid navigation crash
                    val encodedName = URLEncoder.encode(nurse.name, "UTF-8")

                    // Navigate to profile screen
                    navController.navigate("nurseProfile/$encodedName")

                    // Optional: callback
                    onNurseSelected(nurse)
                }
            }

            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

/* ---------------- CARD UI ---------------- */

@Composable
fun NurseCard(item: NurseProfile, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {

        Column(Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEFEFEF))
                )

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF00C853), modifier = Modifier.size(18.dp))
                    }

                    Text(item.qualification, fontSize = 13.sp, color = Color.Gray)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFF9800), modifier = Modifier.size(16.dp))
                        Text("${item.rating}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(" (${item.reviews})  |  ", fontSize = 12.sp, color = Color.Gray)
                        Text(item.experience, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Row {
                item.skills.take(2).forEach { SkillChip(it) }
            }

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Text(item.distance, fontSize = 12.sp, color = Color.Gray)

                Spacer(Modifier.weight(1f))

                Text(item.price, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SkillChip(text: String) {
    Box(
        modifier = Modifier
            .padding(end = 6.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFE6F0FF))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 12.sp, color = Color(0xFF3A6FF3))
    }
}
