package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vaidyacare.network.AdminLoginRequest
import com.example.vaidyacare.network.AdminLoginResponse
import com.example.vaidyacare.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun AdminLoginScreen(
    navController: NavController
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val bg = Brush.verticalGradient(
        listOf(Color(0xFFF9F5FF), Color.White)
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEFE4FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = null,
                    tint = Color(0xFF8E44AD)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Admin Login", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Login in to access the admin dashboard", color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (error.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(error, color = Color.Red, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            // Clear previous error
                            error = ""

                            // Validate input
                            if (email.trim().isEmpty() || password.trim().isEmpty()) {
                                error = "Please enter email and password"
                            } else {
                                loading = true

                                RetrofitClient.api.adminLogin(
                                    AdminLoginRequest(email.trim(), password.trim())
                                ).enqueue(object : Callback<AdminLoginResponse> {

                                    override fun onResponse(
                                        call: Call<AdminLoginResponse>,
                                        response: Response<AdminLoginResponse>
                                    ) {
                                        scope.launch {
                                            loading = false

                                            if (response.isSuccessful) {
                                                val body = response.body()
                                                if (body != null) {
                                                    if (body.success) {
                                                        // ✅ Navigate to AdminDashboard
                                                        navController.navigate("adminDashboard") {
                                                            popUpTo("admin_login") { inclusive = true }
                                                        }
                                                    } else {
                                                        error = body.message ?: "Login failed"
                                                    }
                                                } else {
                                                    error = "Invalid response from server"
                                                }
                                            } else {
                                                // HTTP error (4xx, 5xx)
                                                val errorBody = response.errorBody()?.string()
                                                error = if (errorBody != null) {
                                                    try {
                                                        // Try to parse error response
                                                        val errorResponse = com.google.gson.Gson().fromJson(
                                                            errorBody,
                                                            AdminLoginResponse::class.java
                                                        )
                                                        errorResponse.message ?: "Login failed"
                                                    } catch (_: Exception) {
                                                        "Login failed: ${response.code()}"
                                                    }
                                                } else {
                                                    "Login failed: ${response.code()}"
                                                }
                                            }
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<AdminLoginResponse>,
                                        t: Throwable
                                    ) {
                                        scope.launch {
                                            loading = false
                                            error = "Network error: ${t.message ?: "Please check your connection"}"
                                        }
                                    }
                                })
                            }
                        },
                        enabled = !loading,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8E44AD)
                        )
                    ) {
                        Text(if (loading) "Logging in..." else "Login")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "← Back to role selection",
                color = Color.Gray,
                modifier = Modifier.clickable {
                    navController.popBackStack()
                }
            )
        }
    }
}
