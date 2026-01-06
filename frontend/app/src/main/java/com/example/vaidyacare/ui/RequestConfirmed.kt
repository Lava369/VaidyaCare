package com.example.vaidyacare.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestConfirmedScreen(
    onBackToHome: () -> Unit = {},
    onCancelRequest: () -> Unit = {},
    onTrackLive: () -> Unit = {}
) {
    val clipboard = LocalClipboardManager.current
    val trackingUrl = remember { "https://vaidyaCare.com/track/EMGAPJR3AYW9" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBackToHome) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            item { HeaderSection() }

            item { EmergencyDetailsSection() }

            item {
                LiveTrackingCard(onTrackClick = onTrackLive)
            }

            item {
                ActionButtons(
                    trackingUrl = trackingUrl,
                    onBackToHome = onBackToHome,
                    onCancelRequest = onCancelRequest,
                    clipboardText = clipboard
                )
            }

            // Footer text centered — wrap in Column with horizontalAlignment
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Need help? Call our support at 1800-123-4567",
                        fontSize = 13.sp,
                        color = Color.Red
                    )
                }
            }
        }
    }
}

/* ------------------------ HEADER ------------------------ */
@Composable
fun HeaderSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            "Emergency Request Confirmed!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            "Ambulance is on the way to your location",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

/* ------------------------ DETAILS CARD ------------------------ */
@Composable
fun EmergencyDetailsSection() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DetailItem("Emergency ID", "EMGAPJR3A YW9")
            DetailItem("Vehicle Type", "Emergency Ambulance")
            DetailItem("Pickup Location", "123 Main Street, Downtown Area")
            DetailItem("Estimated Arrival", "8–12 minutes")
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

/* ------------------------ LIVE TRACKING CARD ------------------------ */
@Composable
fun LiveTrackingCard(onTrackClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE53935)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "🔴 Live Tracking Active",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                "Track your ambulance in real-time and get updates on arrival",
                color = Color.White,
                fontSize = 13.sp
            )

            Button(
                onClick = onTrackClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Track Ambulance Live", color = Color(0xFFE53935))
            }
        }
    }
}

/* ------------------------ ACTIONS ------------------------ */
@Composable
fun ActionButtons(
    trackingUrl: String,
    onBackToHome: () -> Unit,
    onCancelRequest: () -> Unit,
    clipboardText: androidx.compose.ui.platform.ClipboardManager
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ActionButtonItem(
                label = "Call Driver",
                icon = Icons.Default.Call,
                onClick = {}
            )

            ActionButtonItem(
                label = "Share Link",
                icon = Icons.Default.Share,
                onClick = {}
            )
        }

        /* Copy Link Section — simple Button with text (no ContentCopy icon) */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF4F4F4), RoundedCornerShape(10.dp))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                trackingUrl,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    clipboardText.setText(AnnotatedString(trackingUrl))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Copy", color = Color.White)
            }
        }

        /* Back to Home */
        Button(
            onClick = onBackToHome,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F6BFF)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Back to Home", color = Color.White)
        }

        /* Cancel Request */
        OutlinedButton(
            onClick = onCancelRequest,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935)),
            border = BorderStroke(1.dp, Color(0xFFE53935)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Cancel Emergency Request")
        }
    }
}

@Composable
fun ActionButtonItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF2F6BFF), modifier = Modifier.size(26.dp))
            Spacer(Modifier.height(6.dp))
            Text(label, fontWeight = FontWeight.Medium)
        }
    }
}
