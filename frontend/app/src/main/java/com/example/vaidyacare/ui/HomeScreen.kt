package com.example.vaidyacare.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vaidyacare.R

@Composable
fun HomeScreen(
    navController: NavController,
    userName: String = "User"
) {
    val context = LocalContext.current
    val activity = context as Activity

    // ✅ BLOCK BACK NAVIGATION TO LOGIN
    BackHandler {
        activity.finish() // Exit app
    }

    val bgGradient = Brush.verticalGradient(
        listOf(Color(0xFFF5F9FF), Color.White)
    )

    Scaffold(
        topBar = { HomeTopBar(navController, userName) },
        bottomBar = { HomeBottomNavBar(navController) }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {

            /* ---------------- HEADER ---------------- */
            item {
                Text(
                    text = "Hello, $userName 👋",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "How can we help you today?",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            item { QuickActionsSection(navController) }
            item { Spacer(modifier = Modifier.height(22.dp)) }
            item { DailyHealthTipCard() }
            item { Spacer(modifier = Modifier.height(22.dp)) }

            item {
                EmergencyCard {
                    navController.navigate("emergencyAmbulance")
                }
            }

            item { Spacer(modifier = Modifier.height(90.dp)) }
        }
    }
}

/* ---------------- TOP BAR ---------------- */

@Composable
fun HomeTopBar(navController: NavController, userName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(id = R.drawable.ic_menu),
            contentDescription = "Menu",
            modifier = Modifier.size(26.dp)
        )

        Text(
            text = "VaidyaCare",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2F6BFF)
        )

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFDDEBFF))
                .clickable { navController.navigate("profile") },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userName.firstOrNull()?.uppercase() ?: "",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/* ---------------- QUICK ACTIONS ---------------- */

@Composable
fun QuickActionsSection(navController: NavController) {
    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickActionItem(
                icon = R.drawable.ic_video,
                title = "Consult\nNow",
                subtitle = "Instant consultation",
                bgColor = Color(0xFFDEE7FF)
            ) { navController.navigate("consultNow") }

            QuickActionItem(
                icon = R.drawable.ic_doctor,
                title = "Find Doctors",
                subtitle = "Specialists",
                bgColor = Color(0xFFDFF7EE)
            ) { navController.navigate("findDoctor") }

            QuickActionItem(
                icon = R.drawable.ic_calendar,
                title = "Book\nAppointment",
                subtitle = "Schedule visit",
                bgColor = Color(0xFFF2E7FF)
            ) {}
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickActionItem(
                icon = R.drawable.ic_report,
                title = "My Reports",
                subtitle = "Prescriptions",
                bgColor = Color(0xFFE6FBE8)
            ) { navController.navigate("myReports") }

            QuickActionItem(
                icon = R.drawable.ic_heart,
                title = "Health Records",
                subtitle = "History",
                bgColor = Color(0xFFFFE4EF)
            ) { navController.navigate("healthRecords") }

            QuickActionItem(
                icon = R.drawable.ic_home_service,
                title = "Home Service",
                subtitle = "Nurse care",
                bgColor = Color(0xFFFFEDE3)
            ) { navController.navigate("homeService") }
        }
    }
}

/* ---------------- QUICK ACTION ITEM ---------------- */

@Composable
fun QuickActionItem(
    icon: Int,
    title: String,
    subtitle: String,
    bgColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(subtitle, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

/* ---------------- DAILY HEALTH TIP ---------------- */

@Composable
fun DailyHealthTipCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAFBF1))
    ) {
        Column(modifier = Modifier.padding(22.dp)) {

            Text(
                "Daily Health Tip",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_droplet),
                    contentDescription = null,
                    modifier = Modifier.size(38.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text("Hydration", color = Color(0xFF2F6BFF))
                    Text("Drink at least 8 glasses of water daily.")
                }
            }
        }
    }
}

/* ---------------- EMERGENCY CARD ---------------- */

@Composable
fun EmergencyCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE32626))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {

            Text(
                "One-Tap Emergency",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                "Instant ambulance dispatch",
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

/* ---------------- BOTTOM NAV ---------------- */

@Composable
fun HomeBottomNavBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(painterResource(R.drawable.ic_home), null) },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("consultNow") },
            icon = { Icon(painterResource(R.drawable.ic_consult), null) },
            label = { Text("Consult") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("myReports") },
            icon = { Icon(painterResource(R.drawable.ic_reports), null) },
            label = { Text("Reports") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("profile") },
            icon = { Icon(painterResource(R.drawable.ic_profile), null) },
            label = { Text("Profile") }
        )
    }
}
