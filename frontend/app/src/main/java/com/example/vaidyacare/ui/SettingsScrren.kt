@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    onPrivacyClicked: () -> Unit = {},
    onLanguageClicked: () -> Unit = {},
    onChangePasswordClicked: () -> Unit = {}
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF2F3F5))
                .padding(16.dp)
        ) {

            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {

                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        "Account Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // PRIVACY (using Settings icon)
                    SettingsItem(
                        icon = Icons.Filled.Settings,
                        iconBg = Color(0xFFEADFFF),
                        title = "Privacy & Security",
                        subtitle = "Manage your privacy settings",
                        onClick = onPrivacyClicked
                    )

                    HorizontalDivider()

                    // LANGUAGE (using Info icon)
                    SettingsItem(
                        icon = Icons.Filled.Info,
                        iconBg = Color(0xFFD9F5E6),
                        title = "Language",
                        subtitle = "English",
                        onClick = onLanguageClicked
                    )

                    HorizontalDivider()

                    // CHANGE PASSWORD
                    SettingsItem(
                        icon = Icons.Filled.Lock,
                        iconBg = Color(0xFFDDE8FF),
                        title = "Change Password",
                        onClick = onChangePasswordClicked
                    )
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("VaidyaCare v1.0.0", fontSize = 13.sp, color = Color.Gray)
                Text("© 2023 VaidyaCare. All rights reserved.", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}


@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String = "",
    onClick: () -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp)
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.Black)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)

            if (subtitle.isNotEmpty()) {
                Text(subtitle, fontSize = 13.sp, color = Color.Gray)
            }
        }

        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Next",
            tint = Color.Gray,
            modifier = Modifier.size(18.dp)
        )
    }
}
