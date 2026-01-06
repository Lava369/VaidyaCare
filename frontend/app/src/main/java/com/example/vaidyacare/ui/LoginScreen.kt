package com.example.vaidyacare.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.RowScope
import com.example.vaidyacare.R
import com.example.vaidyacare.utils.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@Composable
fun PatientLoginScreen(
    onForgotPassword: () -> Unit = {},
    onSignUp: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf("Email") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val isEmailValid = email.contains("@") && email.contains(".")
    val isMobileValid = mobile.length == 10
    val isFormValid =
        password.isNotEmpty() &&
                ((selectedTab == "Email" && isEmailValid) ||
                        (selectedTab == "Mobile" && isMobileValid))

    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF0F5FF), Color.White)
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(75.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF2F6BFF)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_user_blue),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Welcome Back",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(26.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF2F3F5))
                    ) {
                        TabButton(
                            text = "Email",
                            icon = Icons.Default.Email,
                            selected = selectedTab == "Email",
                            onClick = { selectedTab = "Email" }
                        )
                        TabButton(
                            text = "Mobile",
                            icon = Icons.Default.Phone,
                            selected = selectedTab == "Mobile",
                            onClick = { selectedTab = "Mobile" }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (selectedTab == "Email") {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Email") }
                        )
                    } else {
                        OutlinedTextField(
                            value = mobile,
                            onValueChange = {
                                if (it.length <= 10 && it.all(Char::isDigit)) {
                                    mobile = it
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Mobile Number") }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Forgot Password?",
                        color = Color(0xFF2F6BFF),
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { onForgotPassword() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                enabled = isFormValid && !loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = {
                    loading = true

                    scope.launch {
                        try {
                            val json = JSONObject().apply {
                                if (selectedTab == "Email") {
                                    put("email", email.trim())
                                } else {
                                    // Send mobile as-is (backend handles both +91XXXXXXXXXX and XXXXXXXXXX formats)
                                    val mobileToSend = mobile.trim()
                                    // If 10 digits, add +91 to match signup format
                                    val formattedMobile = if (mobileToSend.length == 10 && mobileToSend.all(Char::isDigit)) {
                                        "+91$mobileToSend"
                                    } else {
                                        mobileToSend
                                    }
                                    put("mobile", formattedMobile)
                                }
                                put("password", password)
                            }

                            android.util.Log.d("LoginScreen", "Login request: $json")

                            val body = json.toString()
                                .toRequestBody("application/json".toMediaType())

                            val request = Request.Builder()
                                .url("http://10.26.77.190/vaidyacare/api/login.php")
                                .post(body)
                                .addHeader("Content-Type", "application/json")
                                .build()

                            val response = withContext(Dispatchers.IO) {
                                OkHttpClient().newCall(request).execute()
                            }

                            val result = response.body?.string() ?: ""

                            android.util.Log.d("LoginScreen", "Login response code: ${response.code}")
                            android.util.Log.d("LoginScreen", "Login response: $result")

                            if (result.isBlank()) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Empty server response", Toast.LENGTH_LONG).show()
                                }
                                return@launch
                            }

                            val resJson = JSONObject(result)

                            if (resJson.optBoolean("success", false)) {
                                // Parse user data from response
                                val data = resJson.optJSONObject("data")
                                if (data != null) {
                                    val userId = data.optInt("user_id", 0)
                                    val fullName = data.optString("full_name", "")
                                    val userEmail = data.optString("email", "")
                                    val userMobile = data.optString("mobile", "")

                                    android.util.Log.d("LoginScreen", "Login successful: user_id=$userId, name=$fullName")

                                    // Save user session
                                    UserSession.saveUser(
                                        context = context,
                                        userId = userId,
                                        name = fullName,
                                        email = userEmail,
                                        mobile = userMobile,
                                        patientId = "" // Will be retrieved from profile later
                                    )

                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                                    }
                                    onLoginSuccess()
                                } else {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "Login successful but user data missing", Toast.LENGTH_LONG).show()
                                    }
                                    onLoginSuccess()
                                }
                            } else {
                                val errorMessage = resJson.optString("message", "Login failed. Please try again.")
                                android.util.Log.e("LoginScreen", "Login failed: $errorMessage")
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        errorMessage,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                        } catch (e: Exception) {
                            android.util.Log.e("LoginScreen", "Login error: ${e.message}", e)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Login error: ${e.localizedMessage ?: e.message ?: "Please try again"}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } finally {
                            loading = false
                        }
                    }
                }
            ) {
                Text(if (loading) "Please wait..." else "Login")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                Text("Don't have an account?")
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Sign Up",
                    color = Color(0xFF2F6BFF),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSignUp() }
                )
            }
        }
    }
}

@Composable
fun RowScope.TabButton(
    text: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) Color(0xFF2F6BFF) else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (selected) Color.White else Color.Black
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text, color = if (selected) Color.White else Color.Black)
        }
    }
}
