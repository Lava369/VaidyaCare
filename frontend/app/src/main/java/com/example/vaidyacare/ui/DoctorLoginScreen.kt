@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.vaidyacare.utils.DoctorSession
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

@Composable
fun DoctorLoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: (Int) -> Unit,
    onSignup: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val context = LocalContext.current
    
    var isEmailLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val client = remember { OkHttpClient() }
    val scope = rememberCoroutineScope()

    fun login() {
        val input = if (isEmailLogin) email.trim() else mobile.trim()
        val pass = password.trim()

        if (input.isEmpty() || pass.isEmpty()) {
            errorMessage = "Please enter all fields"
            return
        }

        loading = true
        errorMessage = ""

        val json = JSONObject().apply {
            if (isEmailLogin) put("email", input) else put("mobile", input)
            put("password", pass)
        }

        val body = json.toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("http://10.26.77.190/vaidyacare/api/doctor_login.php")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                scope.launch {
                    loading = false
                    errorMessage = "Network error: ${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body?.string().orEmpty()

                scope.launch {
                    loading = false
                    try {
                        val jsonRes = JSONObject(res)
                        val success = jsonRes.optBoolean("success")
                        val message = jsonRes.optString("message")

                        if (success) {
                            val doctor = jsonRes.getJSONObject("doctor")
                            val doctorId = doctor.optInt("doctor_id", 0)
                            val fullName = doctor.optString("full_name", "")
                            val doctorEmail = doctor.optString("email", "")
                            val doctorMobile = doctor.optString("mobile", "")
                            val licenseNo = doctor.optString("license_no", "")

                            // Save doctor session
                            if (doctorId > 0) {
                                DoctorSession.saveDoctor(
                                    context = context,
                                    doctorId = doctorId,
                                    name = fullName,
                                    email = doctorEmail,
                                    mobile = doctorMobile,
                                    license = licenseNo
                                )
                            }

                            // ✅ LOGIN SUCCESS → NAVIGATE
                            onLoginSuccess(doctorId)
                        } else {
                            errorMessage = message
                        }
                    } catch (_: Exception) {
                        errorMessage = "Invalid server response"
                    }
                }
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Doctor Login") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF2FFF6))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF2ECC71)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, tint = Color.White)
            }

            Spacer(Modifier.height(16.dp))

            Text("Doctor Portal", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Login to manage your consultations", fontSize = 13.sp, color = Color.Gray)

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(Modifier.padding(20.dp)) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF3F3F3), RoundedCornerShape(14.dp))
                            .padding(4.dp)
                    ) {
                        ToggleButton(isEmailLogin, "Email", Icons.Default.Email) {
                            isEmailLogin = true
                        }
                        ToggleButton(!isEmailLogin, "Mobile", Icons.Default.Phone) {
                            isEmailLogin = false
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = if (isEmailLogin) email else mobile,
                        onValueChange = {
                            if (isEmailLogin) email = it else mobile = it
                        },
                        label = { Text(if (isEmailLogin) "Email" else "Mobile") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(errorMessage, color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Forgot Password?",
                        color = Color(0xFF2ECC71),
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { onForgotPassword() }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { login() },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (loading) "Logging in..." else "Login")
            }

            Spacer(Modifier.height(16.dp))

            Row {
                Text("Don't have an account? ")
                Text(
                    "Sign Up",
                    color = Color(0xFF2ECC71),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSignup() }
                )
            }
        }
    }
}

@Composable
private fun RowScope.ToggleButton(
    selected: Boolean,
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Color(0xFF2ECC71) else Color.Transparent)
            .clickable { onClick() }
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = if (selected) Color.White else Color.Gray)
        Spacer(Modifier.width(6.dp))
        Text(text, color = if (selected) Color.White else Color.Gray)
    }
}
