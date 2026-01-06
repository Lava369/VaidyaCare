@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vaidyacare.R

/* ----------------------- DATA ----------------------- */

data class RecordItem(
    val title: String,
    val category: String,
    val date: String,
    val doctor: String,
    val icon: Int
)

val recentRecords = listOf(
    RecordItem("Blood Test Results", "Lab Report", "2024-01-15", "Dr. Sarah Johnson", R.drawable.ic_lab),
    RecordItem("Prescription - Antibiotics", "Prescription", "2024-01-12", "Dr. Michael Chen", R.drawable.ic_prescription),
    RecordItem("Annual Checkup", "Medical History", "2024-01-08", "Dr. Emily Brown", R.drawable.ic_history)
)

/* ----------------------- MAIN SCREEN ----------------------- */

@Composable
fun HealthRecordsScreen(
    onBack: () -> Unit = {},
    onUpload: () -> Unit = {}
) {

    val bgGradient = Brush.verticalGradient(
        listOf(Color(0xFFF5F9FF), Color.White)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Health Records", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Manage all your medical documents", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item { UploadCard(onUpload) }

            item {
                Text("Categories", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
            }

            item { CategoriesGrid() }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Recent Records", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("View All", color = Color(0xFF2F6BFF), fontSize = 13.sp)
                }
            }

            items(recentRecords.size) { index ->
                RecordCard(recentRecords[index])
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

/* ----------------------- UPLOAD CARD ----------------------- */

@Composable
fun UploadCard(onUpload: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(Color(0xFF2F6BFF)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text("Upload New Record", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Add prescriptions, reports, or documents", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
            }

            Card(
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(50.dp),
                colors = CardDefaults.cardColors(Color.White)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_upload),
                    contentDescription = null,
                    tint = Color(0xFF2F6BFF),
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

/* ----------------------- CATEGORY GRID ----------------------- */

@Composable
fun CategoriesGrid() {

    Column {

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CategoryCard("Prescriptions", "12 records", R.drawable.ic_prescription)
            CategoryCard("Lab Reports", "8 records", R.drawable.ic_lab)
        }

        Spacer(Modifier.height(14.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CategoryCard("Medical History", "5 records", R.drawable.ic_history)
            CategoryCard("Vaccination", "6 records", R.drawable.ic_vaccine)
        }

        Spacer(Modifier.height(14.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CategoryCard("Allergies", "3 records", R.drawable.ic_allergy)
            CategoryCard("Insurance", "2 records", R.drawable.ic_insurance)
        }
    }
}

@Composable
fun CategoryCard(title: String, count: String, icon: Int) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {

        Column(
            Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Color(0xFF2F6BFF),
                modifier = Modifier.size(28.dp)
            )

            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(count, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

/* ----------------------- RECORD CARD ----------------------- */

@Composable
fun RecordCard(item: RecordItem) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {

        Column(Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF2F2F2)),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = null,
                        tint = Color(0xFF2F6BFF),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(item.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(item.category, fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.height(4.dp))
                    Text("📅 ${item.date}", fontSize = 12.sp, color = Color.Gray)
                    Text("By ${item.doctor}", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}
