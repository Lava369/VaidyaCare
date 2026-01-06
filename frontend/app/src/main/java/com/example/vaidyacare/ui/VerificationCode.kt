@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vaidyacare.R
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@Composable
fun VerificationCodeScreen(
    onBack: () -> Unit = {},
    onVerifySuccess: () -> Unit = {}
) {
    // ✅ EMAIL ONLY
    var selectedTab by remember { mutableStateOf("Email") }
    var timer by remember { mutableIntStateOf(240) }

    // ⚠️ Replace this with real userId from previous screen
    val userId = remember { 10 }   // example: same user_id used while generating OTP

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val otp = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = List(6) { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
        while (timer > 0) {
            delay(1000)
            timer--
        }
    }

    val bgGradient = Brush.verticalGradient(
        listOf(Color(0xFFF0F5FF), Color.White)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verify OTP") },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .clickable { onBack() }
                    )
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(padding)
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDDEBFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_user_blue),
                    contentDescription = null,
                    tint = Color(0xFF2F6BFF),
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("Enter Verification Code", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Text(
                "We've sent a 6-digit code to your registered email",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // ✅ EMAIL TAB ONLY
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF2F3F5)),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TabItem("Email", Icons.Default.Email, true) {}
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        otp.forEachIndexed { index, value ->
                            OTPBox(
                                value = value,
                                focusRequester = focusRequesters[index],
                                onValueChange = { input ->
                                    val digit = input.takeLast(1)
                                    if (digit.all { it.isDigit() }) {
                                        otp[index] = digit
                                        if (index < 5)
                                            focusRequesters[index + 1].requestFocus()
                                    } else if (input.isEmpty()) {
                                        otp[index] = ""
                                        if (index > 0)
                                            focusRequesters[index - 1].requestFocus()
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val otpValue = otp.joinToString("")
                            if (otpValue.length != 6) {
                                Toast.makeText(context, "Enter valid OTP", Toast.LENGTH_LONG).show()
                                return@Button
                            }

                            scope.launch {
                                try {
                                    val json = JSONObject().apply {
                                        put("user_id", userId)
                                        put("otp", otpValue)
                                    }

                                    val body = json.toString()
                                        .toRequestBody("application/json".toMediaType())

                                    val request = Request.Builder()
                                        .url("http://10.26.77.190/vaidyacare/api/verify_reset_otp.php")
                                        .post(body)
                                        .build()

                                    val response = withContext(Dispatchers.IO) {
                                        OkHttpClient().newCall(request).execute()
                                    }

                                    val resJson = JSONObject(response.body?.string() ?: "")
                                    val isSuccess = resJson.optBoolean("success", false)

                                    if (!isSuccess) {
                                        Toast.makeText(
                                            context,
                                            resJson.optString("message", "Invalid OTP"),
                                            Toast.LENGTH_LONG
                                        ).show()
                                        return@launch
                                    }

                                    Toast.makeText(context, "OTP Verified", Toast.LENGTH_LONG).show()
                                    onVerifySuccess()

                                } catch (_: Exception) {
                                    Toast.makeText(context, "Server not reachable", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF2F6BFF))
                    ) {
                        Text("Verify & Continue", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun OTPBox(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .size(44.dp)
            .focusRequester(focusRequester),
        singleLine = true,
        maxLines = 1,
        textStyle = TextStyle(
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = VisualTransformation.None
    )
}

@Composable
fun RowScope.TabItem(
    label: String,
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
            Icon(icon, null, tint = Color.White)
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, color = Color.White)
        }
    }
}
