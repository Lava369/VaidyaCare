@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SyncInformation(onBack: () -> Unit = {}) {

    var autoSync by remember { mutableStateOf(true) }
    var wifiOnly by remember { mutableStateOf(true) }
    var backgroundSync by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sync Information", fontSize = 20.sp) },
                navigationIcon = {
                    Text(
                        text = "←",
                        fontSize = 26.sp,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable { onBack() }
                    )
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())   // ✅ ENABLE SCROLL
        ) {

            CloudSyncCard()

            Spacer(modifier = Modifier.height(20.dp))

            Text("Sync Status", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(12.dp))

            SyncStatusItem("Medical Records", "2 minutes ago", "14.3 MB")
            SyncStatusItem("Appointments", "5 minutes ago", "0.5 MB")
            SyncStatusItem("Prescriptions", "10 minutes ago", "1.2 MB")
            SyncStatusItem("Health Timeline", "15 minutes ago", "2.8 MB")

            Spacer(modifier = Modifier.height(25.dp))

            Text("Sync Settings", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(12.dp))

            SettingToggleItem("Auto Sync", "Automatically sync data", autoSync) { autoSync = it }
            SettingToggleItem("Sync on WiFi Only", "Save mobile data", wifiOnly) { wifiOnly = it }
            SettingToggleItem("Background Sync", "Sync when app is closed", backgroundSync) { backgroundSync = it }

            Spacer(modifier = Modifier.height(25.dp))

            StorageUsageCard()

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun CloudSyncCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1967D2)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Text("☁️", fontSize = 34.sp)

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text("Cloud Sync Active", color = Color.White, fontSize = 18.sp)
                    Text(
                        "Your data is automatically synced across all devices",
                        color = Color(0xFFD2E3FC),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Sync Now", color = Color(0xFF1967D2))
            }
        }
    }
}

@Composable
fun SyncStatusItem(title: String, time: String, size: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(time, fontSize = 12.sp, color = Color.Gray)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("Synced ✓", color = Color(0xFF2E7D32), fontSize = 12.sp)
                Text(size, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun SettingToggleItem(
    title: String,
    description: String,
    value: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(description, fontSize = 12.sp, color = Color.Gray)
        }

        Switch(checked = value, onCheckedChange = onToggle)
    }
}

@Composable
fun StorageUsageCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Storage Usage", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(10.dp))
            Text("Cloud Storage", fontSize = 14.sp, color = Color.Gray)

            Spacer(Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { 18.8f / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )

            Spacer(Modifier.height(6.dp))

            Text("18.8 MB / 100 MB", fontSize = 12.sp, color = Color.Gray)
        }
    }
}
