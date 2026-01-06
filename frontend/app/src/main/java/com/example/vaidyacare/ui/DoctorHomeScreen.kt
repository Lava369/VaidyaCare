@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vaidyacare.R

/* ---------------- DOCTOR HOME SCREEN ---------------- */

@Composable
fun DoctorHomeScreen(
    doctorId: Int,
    navController: NavController
) {

    Scaffold(
        topBar = { DoctorTopBar() },
        bottomBar = { DoctorBottomBar(navController) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            Text(
                text = "Hello, Doctor 👋",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Here is your overview for today",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(20.dp))

            DashboardItem(
                iconRes = R.drawable.ic_calendar,
                title = "Schedule",
                subtitle = "View your calendar & appointments",
                badge = "Nov",
                badgeColor = Color(0xFF2563EB),
                onClick = { navController.navigate("schedule") }
            )

            DashboardItem(
                iconRes = R.drawable.ic_video,
                title = "Pending Consultations",
                subtitle = "Complete waiting consultations",
                badge = "8",
                badgeColor = Color(0xFF22C55E),
                onClick = { navController.navigate("pendingConsultations") }
            )

            DashboardItem(
                iconRes = R.drawable.ic_message,
                title = "Patient Messages",
                subtitle = "New messages from patients",
                badge = "5",
                badgeColor = Color(0xFF9333EA),
                onClick = { navController.navigate("patientMessages") }
            )

            DashboardItem(
                iconRes = R.drawable.ic_folder,
                title = "Medical Records",
                subtitle = "Access patient history & reports",
                badge = "144",
                badgeColor = Color(0xFFF97316),
                onClick = { navController.navigate("medicalRecords") }
            )

            DashboardItem(
                iconRes = R.drawable.ic_money,
                title = "Earnings & Payments",
                subtitle = "Daily & monthly earnings",
                badge = "₹45.5k",
                badgeColor = Color(0xFF10B981),
                onClick = { navController.navigate("earningPayments") }
            )

            DashboardItem(
                iconRes = R.drawable.ic_time,
                title = "Availability Schedule",
                subtitle = "Update your working hours",
                badge = "OPEN",
                badgeColor = Color(0xFF2563EB),
                onClick = { navController.navigate("availabilitySchedule") }
            )

            DashboardItem(
                iconRes = R.drawable.ic_star,
                title = "Reviews & Ratings",
                subtitle = "Check feedback from patients",
                badge = "4.8★",
                badgeColor = Color(0xFFF59E0B),
                onClick = { navController.navigate("reviewRatings") }
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Quick Actions",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { navController.navigate("startConsultation") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Start Consultation")
            }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = { navController.navigate("updatePatientReport") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Upload Patient Report")
            }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = { navController.navigate("updateStatus") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Update Status")
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

/* ---------------- DASHBOARD ITEM ---------------- */

@Composable
fun DashboardItem(
    iconRes: Int,
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(badgeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = badgeColor
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, fontSize = 13.sp, color = Color.Gray)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(badgeColor)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(badge, color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

/* ---------------- TOP BAR ---------------- */

@Composable
fun DoctorTopBar() {
    TopAppBar(
        title = { Text("VaidyaCare Doctor") }
    )
}

/* ---------------- BOTTOM BAR ---------------- */

@Composable
fun DoctorBottomBar(navController: NavController) {
    NavigationBar {

        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(painterResource(R.drawable.ic_home), null) },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("schedule") },
            icon = { Icon(painterResource(R.drawable.ic_calendar), null) },
            label = { Text("Schedule") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("notifications") },
            icon = { Icon(painterResource(R.drawable.ic_notification), null) },
            label = { Text("Messages") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("doctorProfile") },
            icon = { Icon(painterResource(R.drawable.ic_profile), null) },
            label = { Text("Profile") }
        )
    }
}
