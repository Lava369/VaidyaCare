package com.example.vaidyacare.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.vaidyacare.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit = {},
    onSendCodeSuccess: (String) -> Unit = {}, // 👉 navigate to OTP screen with email
    onBackToLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val gradient = Brush.verticalGradient(
        listOf(Color(0xFFEFF5FF), Color.White)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(horizontal = 20.dp)
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        // 🔙 Back
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onBack() }
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null)
            Spacer(modifier = Modifier.width(10.dp))
            Text("Forgot Password", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(30.dp))

        // 🔑 Icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF2F6BFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_key),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Reset Password", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Enter your registered email to receive a verification code",
            fontSize = 14.sp,
            color = Color(0xFF6B7A89)
        )

        Spacer(modifier = Modifier.height(25.dp))

        // 📧 Email Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Email Address", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter your registered email") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null)
                    },
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        // 🔵 Send OTP Button
        Button(
            onClick = {
                if (email.isBlank()) {
                    Toast.makeText(context, "Enter email", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true

                scope.launch {
                    sendOtpApi(
                        email = email,
                        onSuccess = {
                            isLoading = false
                            Toast.makeText(context, "OTP sent", Toast.LENGTH_SHORT).show()
                            onSendCodeSuccess(email)
                        },
                        onError = {
                            isLoading = false
                            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                        }
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(Color(0xFF2F6BFF))
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Send Verification Code", color = Color.White, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Back to Login
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Remember your password?", color = Color.Gray)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                "Back to Login",
                color = Color(0xFF2F6BFF),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onBackToLogin() }
            )
        }
    }
}

/* ================= API CALL ================= */

suspend fun sendOtpApi(
    email: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()

            val body = FormBody.Builder()
                .add("email", email)
                .build()

            val request = Request.Builder()
                .url("http://10.26.77.190/vaidyacare/api/send_otp.php")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val json = JSONObject(response.body?.string() ?: "{}")

            if (json.optString("status") == "success") {
                withContext(Dispatchers.Main) { onSuccess() }
            } else {
                withContext(Dispatchers.Main) {
                    onError(json.optString("message", "Something went wrong"))
                }
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onError("Server error. Try again.")
            }
        }
    }
}
