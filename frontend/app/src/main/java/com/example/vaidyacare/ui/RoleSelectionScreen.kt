package com.example.vaidyacare.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vaidyacare.R

@Composable
fun RoleSelectionScreen(
    navController: NavController
) {

    val bg = Brush.verticalGradient(
        listOf(Color(0xFFF0F5FF), Color.White)
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(horizontal = 20.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.heartbeat),
                contentDescription = null,
                modifier = Modifier.size(60.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Welcome to VaidyaCare", fontSize = 22.sp)
            Text("Select your role to continue", color = Color.Gray)

            Spacer(modifier = Modifier.height(32.dp))

            RoleCard(
                title = "Patient",
                subtitle = "Book appointments and consult doctors",
                icon = Icons.Outlined.Person,
                iconBackground = Color(0xFFDCEBFF),
                iconTint = Color(0xFF3E7BFA),
                onClick = { navController.navigate("loginScreen") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RoleCard(
                title = "Doctor",
                subtitle = "Manage consultations and patients",
                icon = Icons.Filled.Person,
                iconBackground = Color(0xFFE9F9EC),
                iconTint = Color(0xFF34C759),
                onClick = { navController.navigate("doctorLogin") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ✅ ONLY CHANGE IS HERE
            RoleCard(
                title = "Admin",
                subtitle = "Manage system and user accounts",
                icon = Icons.Filled.Settings,
                iconBackground = Color(0xFFF2E8FF),
                iconTint = Color(0xFF9B59FF),
                onClick = { navController.navigate("adminLogin") }
            )
        }
    }
}

@Composable
fun RoleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBackground: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(title, fontSize = 18.sp)
                Text(subtitle, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}
