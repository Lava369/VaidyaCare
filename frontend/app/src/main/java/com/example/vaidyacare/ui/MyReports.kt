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

/* ----------------------- DATA MODEL ----------------------- */

data class Report(
    val title: String,
    val category: String,
    val date: String,
    val doctor: String,
    val icon: Int
)

/* SAMPLE DATA */
val sampleReports = listOf(
    Report("Blood Test Report", "Lab Report", "2024-01-15", "Dr. Sarah Johnson", R.drawable.ic_droplet),
    Report("ECG Report", "Diagnostic", "2024-01-10", "Dr. Michael Chen", R.drawable.ic_heart),
    Report("X-Ray Chest", "Imaging", "2024-01-05", "Dr. Emily Brown", R.drawable.ic_bone),
    Report("Diabetes Checkup", "Lab Report", "2023-12-20", "Dr. Sarah Johnson", R.drawable.ic_droplet)
)

/* ----------------------- MAIN SCREEN ----------------------- */

@Composable
fun MyReportsScreen(
    onBack: () -> Unit = {},
    onGoToHealthRecords: () -> Unit = {}
) {

    val bgGradient = Brush.verticalGradient(
        listOf(Color(0xFFF5F9FF), Color.White)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("My Reports", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(
                            "View and download your medical reports",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
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
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            /* STATS ROW - apply weight here inside Row scope */
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total Reports",
                        value = "4",
                        bg = Color(0xFFE8F0FF),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "This Month",
                        value = "2",
                        bg = Color(0xFFE6F8E8),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Lab Tests",
                        value = "3",
                        bg = Color(0xFFF8E9FF),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text("Recent Reports", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            items(sampleReports.size) { idx ->
                ReportCard(report = sampleReports[idx])
            }

            item {
                UploadMoreCard(onClick = onGoToHealthRecords)
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

/* ----------------------- STAT CARD ----------------------- */

/**
 * StatCard now accepts a [modifier] so parent Row can pass in Modifier.weight(1f).
 */
@Composable
fun StatCard(title: String, value: String, bg: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

/* ----------------------- REPORT CARD ----------------------- */

@Composable
fun ReportCard(report: Report) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {

        Column(Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF6F6F6)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = report.icon),
                        contentDescription = null,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(report.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(report.category, fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.height(4.dp))
                    Text("📅 ${report.date}", fontSize = 12.sp, color = Color.Gray)
                    Text("By ${report.doctor}", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Button(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F6BFF)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("View", color = Color.White)
                }

                OutlinedButton(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_download),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Download")
                }
            }
        }
    }
}

/* ----------------------- CTA CARD ----------------------- */

@Composable
fun UploadMoreCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2F6BFF)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text("Need to upload a report?", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("Keep all your medical records in one place", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.height(40.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Go to Health Records", color = Color(0xFF2F6BFF))
            }
        }
    }
}
