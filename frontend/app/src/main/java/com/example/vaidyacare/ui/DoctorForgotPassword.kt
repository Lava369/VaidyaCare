@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DoctorForgotPasswordScreen(
    onBack: () -> Unit = {},
    onSendCode: (String, Boolean) -> Unit = { _, _ -> },
    onBackToLogin: () -> Unit = {}
) {

    var isEmailSelected by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }

    val green = Color(0xFF2ECC71)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Forgot Password") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF2FBF6)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(40.dp))

            /* ---------- ICON ---------- */
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(green),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Reset Password",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = if (isEmailSelected)
                    "Enter your email to receive a verification code"
                else
                    "Enter your mobile to receive a verification code",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(24.dp))

            /* ---------- CARD ---------- */
            Card(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {

                Column(Modifier.padding(20.dp)) {

                    /* ---------- TOGGLE ---------- */
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                            .padding(4.dp)
                    ) {

                        ToggleButton(
                            modifier = Modifier.weight(1f),
                            selected = isEmailSelected,
                            text = "Email",
                            icon = Icons.Default.Email
                        ) {
                            isEmailSelected = true
                        }

                        Spacer(Modifier.width(6.dp))

                        ToggleButton(
                            modifier = Modifier.weight(1f),
                            selected = !isEmailSelected,
                            text = "Mobile",
                            icon = Icons.Default.Phone
                        ) {
                            isEmailSelected = false
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    /* ---------- INPUT ---------- */
                    if (isEmailSelected) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            placeholder = { Text("Enter your registered email") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    } else {
                        OutlinedTextField(
                            value = mobile,
                            onValueChange = { mobile = it },
                            label = { Text("Mobile Number") },
                            placeholder = { Text("Enter your registered mobile number") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            /* ---------- BUTTON ---------- */
            Button(
                onClick = {
                    onSendCode(
                        if (isEmailSelected) email else mobile,
                        isEmailSelected
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = green)
            ) {
                Text("Send Verification Code", fontSize = 16.sp)
            }

            Spacer(Modifier.height(20.dp))

            /* ---------- FOOTER ---------- */
            Row {
                Text("Remember your password? ")
                Text(
                    text = "Back to Login",
                    color = green,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onBackToLogin() }
                )
            }
        }
    }
}

/* ---------- TOGGLE BUTTON ---------- */
@Composable
private fun ToggleButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val green = Color(0xFF2ECC71)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) green else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (selected) Color.White else Color.Gray,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text,
            color = if (selected) Color.White else Color.Gray,
            fontWeight = FontWeight.Medium
        )
    }
}