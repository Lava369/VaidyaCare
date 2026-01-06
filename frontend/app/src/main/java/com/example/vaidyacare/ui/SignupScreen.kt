package com.example.vaidyacare.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vaidyacare.R
import com.example.vaidyacare.network.RetrofitClient
import com.example.vaidyacare.network.SignupRequest
import com.example.vaidyacare.network.SignupResponse
import com.example.vaidyacare.utils.UserSession
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

private fun isValidEmail(email: String): Boolean {
    val emailPattern = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        Pattern.CASE_INSENSITIVE
    )
    return emailPattern.matcher(email).matches()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    onBack: () -> Unit,
    onLoginClick: () -> Unit
) {

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var countryCode by remember { mutableStateOf("+91") }
    var countryExpanded by remember { mutableStateOf(false) }
    var mobile by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var genderExpanded by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreeTerms by remember { mutableStateOf(false) }

    var errorMsg by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val bgGradient = Brush.verticalGradient(
        listOf(Color(0xFFF0F5FF), Color.White)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Patient Sign Up") },
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
                .background(bgGradient)
                .padding(padding)
                .padding(horizontal = 22.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDDEBFF)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_user_blue),
                    contentDescription = null,
                    modifier = Modifier.size(45.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            Text("Create Account", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Join VaidyaCare for better healthcare", fontSize = 14.sp, color = Color.Gray)

            Spacer(Modifier.height(22.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {

                Column(modifier = Modifier.padding(20.dp)) {

                    OutlinedTextField(fullName, { fullName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(email, { email = it }, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))

                    Row {
                        Box(modifier = Modifier.weight(0.35f)) {
                            OutlinedTextField(
                                value = countryCode,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Code") },
                                trailingIcon = {
                                    Icon(Icons.Default.ArrowDropDown, null,
                                        Modifier.clickable { countryExpanded = true })
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            DropdownMenu(
                                expanded = countryExpanded,
                                onDismissRequest = { countryExpanded = false }
                            ) {
                                listOf("+91", "+1", "+44").forEach {
                                    DropdownMenuItem(
                                        text = { Text(it) },
                                        onClick = {
                                            countryCode = it
                                            countryExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.width(12.dp))

                        OutlinedTextField(
                            value = mobile,
                            onValueChange = {
                                if (it.length <= 10 && it.all(Char::isDigit)) {
                                    mobile = it
                                }
                            },
                            label = { Text("Mobile Number") },
                            modifier = Modifier.weight(0.65f),
                            singleLine = true
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row {
                        OutlinedTextField(age, { age = it }, label = { Text("Age") }, modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(12.dp))

                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = gender,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Gender") },
                                trailingIcon = {
                                    Icon(Icons.Default.ArrowDropDown, null,
                                        Modifier.clickable { genderExpanded = true })
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            DropdownMenu(
                                expanded = genderExpanded,
                                onDismissRequest = { genderExpanded = false }
                            ) {
                                listOf("Male", "Female", "Other").forEach {
                                    DropdownMenuItem(
                                        text = { Text(it) },
                                        onClick = {
                                            gender = it
                                            genderExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(password, { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth())

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(confirmPassword, { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth())

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(agreeTerms, { agreeTerms = it })
                        Text("I agree to the Terms & Conditions and Privacy Policy")
                    }

                    if (errorMsg.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = errorMsg,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                enabled = agreeTerms && !loading,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                onClick = {

                    when {
                        fullName.isBlank() -> errorMsg = "Full name required"
                        email.isBlank() -> errorMsg = "Email required"
                        !isValidEmail(email.trim()) -> errorMsg = "Please enter a valid email address"
                        mobile.length != 10 -> errorMsg = "Mobile must be 10 digits"
                        age.isBlank() || age.toIntOrNull() == null || age.toInt() <= 0 -> errorMsg = "Please enter a valid age"
                        password.length < 6 -> errorMsg = "Password must be at least 6 characters"
                        password != confirmPassword -> errorMsg = "Passwords do not match"
                        else -> {
                            loading = true
                            errorMsg = ""

                            // Combine country code with mobile number (backend expects format like +919573771937)
                            val fullMobile = "${countryCode}${mobile.trim()}"

                            android.util.Log.d("SignupScreen", "Signup request: full_name=${fullName.trim()}, email=${email.trim()}, mobile=$fullMobile, age=${age.toInt()}, gender=$gender")

                            RetrofitClient.api.signup(
                                SignupRequest(
                                    full_name = fullName.trim(),
                                    email = email.trim(),
                                    mobile = fullMobile,
                                    age = age.toInt(),
                                    gender = gender,
                                    password = password
                                )
                            ).enqueue(object : Callback<SignupResponse> {

                                override fun onResponse(
                                    call: Call<SignupResponse>,
                                    response: Response<SignupResponse>
                                ) {
                                    scope.launch {
                                        loading = false
                                        
                                        android.util.Log.d("SignupScreen", "Response code: ${response.code()}")
                                        android.util.Log.d("SignupScreen", "Response body: ${response.body()}")
                                        
                                        if (!response.isSuccessful) {
                                            // Try to parse error response
                                            try {
                                                val errorBody = response.errorBody()?.string()
                                                android.util.Log.e("SignupScreen", "Error response: $errorBody")
                                                
                                                if (errorBody != null) {
                                                    val gson = com.google.gson.Gson()
                                                    val errorResponse = gson.fromJson(errorBody, SignupResponse::class.java)
                                                    errorMsg = errorResponse.message ?: "Signup failed. Please try again."
                                                } else {
                                                    errorMsg = "Signup failed. Error code: ${response.code()}"
                                                }
                                            } catch (e: Exception) {
                                                android.util.Log.e("SignupScreen", "Error parsing error response: ${e.message}", e)
                                                errorMsg = "Signup failed. Please try again."
                                            }
                                            return@launch
                                        }
                                        
                                        val body = response.body()

                                        if (body != null && body.success && body.data != null) {
                                            val d = body.data

                                            android.util.Log.d("SignupScreen", "Signup successful: user_id=${d.user_id}")

                                            UserSession.saveUser(
                                                context = context,
                                                userId = d.user_id,
                                                name = d.full_name,
                                                email = d.email,
                                                mobile = d.mobile,
                                                patientId = ""   // Will be generated/retrieved later
                                            )

                                            onLoginClick()
                                        } else {
                                            val errorMessage = body?.message ?: "Signup failed. Please try again."
                                            android.util.Log.e("SignupScreen", "Signup failed: $errorMessage")
                                            errorMsg = errorMessage
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                                    scope.launch {
                                        loading = false
                                        android.util.Log.e("SignupScreen", "Network error: ${t.message}", t)
                                        errorMsg = "Network error. Please check your connection and try again."
                                    }
                                }
                            })
                        }
                    }
                }
            ) {
                Text(if (loading) "Creating..." else "Create Account")
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}
