@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.vaidyacare.utils.UserSession
import com.example.vaidyacare.network.RetrofitClient
import kotlinx.coroutines.launch
import java.util.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

@Composable
fun EditProfileScreen(onBack: () -> Unit = {}) {

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val client = remember { OkHttpClient() }

    // ✅ LOAD DATA FROM SIGNUP (UserSession)
    var fullName by remember { mutableStateOf(UserSession.getName(ctx)) }
    val email = UserSession.getEmail(ctx)          // 🔒 fixed
    val phone = UserSession.getMobile(ctx)         // 🔒 fixed

    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var isGenderExpanded by remember { mutableStateOf(false) }
    var bloodGroup by remember { mutableStateOf("") }
    var isBloodExpanded by remember { mutableStateOf(false) }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }

    // Load profile data from API
    LaunchedEffect(Unit) {
        if (email.isNotEmpty()) {
            try {
                val response = RetrofitClient.api.getProfile(email)
                if (response.success) {
                    val data = response.data
                    fullName = data.full_name.takeIf { it.isNotEmpty() } ?: fullName
                    dob = data.dob ?: ""
                    gender = data.gender ?: "Male"
                    bloodGroup = data.blood_group ?: ""
                    height = data.height_cm ?: ""
                    weight = data.weight_kg ?: ""
                    address = data.address ?: ""
                    city = data.city ?: ""
                    state = data.state ?: ""
                    pin = data.pin ?: ""
                }
            } catch (e: Exception) {
                android.util.Log.e("EditProfile", "Error loading profile: ${e.message}")
            } finally {
                loading = false
            }
        } else {
            loading = false
        }
    }

    // Function to show date picker - creates dialog dynamically
    fun showDatePicker() {
        val initialCalendar = Calendar.getInstance()
        
        // If dob is set, use it; otherwise use current date
        if (dob.isNotEmpty()) {
            try {
                val parts = dob.split("-")
                if (parts.size == 3) {
                    initialCalendar.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
                }
            } catch (e: Exception) {
                // Use current date if parsing fails
                android.util.Log.e("EditProfile", "Error parsing DOB: ${e.message}")
            }
        }
        
        val dialog = DatePickerDialog(
            ctx,
            { _, year, month, day ->
                val monthFormatted = (month + 1).toString().padStart(2, '0')
                val dayFormatted = day.toString().padStart(2, '0')
                dob = "$year-$monthFormatted-$dayFormatted"
            },
            initialCalendar.get(Calendar.YEAR),
            initialCalendar.get(Calendar.MONTH),
            initialCalendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(Color(0xFFF2F4F7))
                    .padding(16.dp)
            ) {

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            /* ---------- PROFILE IMAGE ---------- */
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE6ECF5)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(50.dp)
                                    )
                                }
                                Icon(
                                    Icons.Default.Edit,
                                    null,
                                    tint = Color.Blue,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .padding(5.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            /* ---------- EDITABLE ---------- */
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text("Full Name") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            /* ---------- 🔒 NON-EDITABLE EMAIL ---------- */
                            OutlinedTextField(
                                value = email,
                                onValueChange = {},
                                label = { Text("Email Address") },
                                enabled = false,
                                modifier = Modifier.fillMaxWidth()
                            )

                            /* ---------- 🔒 NON-EDITABLE Mobile---------- */
                            OutlinedTextField(
                                value = phone,
                                onValueChange = {},
                                label = { Text("Mobile Number") },
                                enabled = false,
                                modifier = Modifier.fillMaxWidth()
                            )

                            /* ---------- DOB ---------- */
                            OutlinedTextField(
                                value = if (dob.isNotEmpty()) {
                                    try {
                                        val parts = dob.split("-")
                                        if (parts.size == 3) {
                                            "${parts[2]}/${parts[1]}/${parts[0]}" // DD/MM/YYYY format for display
                                        } else {
                                            dob
                                        }
                                    } catch (e: Exception) {
                                        dob
                                    }
                                } else {
                                    "Select Date of Birth"
                                },
                                onValueChange = {},
                                readOnly = true,
                                enabled = true,
                                label = { Text("Date of Birth") },
                                trailingIcon = {
                                    IconButton(
                                        onClick = { showDatePicker() }
                                    ) {
                                        Icon(
                                            Icons.Default.DateRange,
                                            contentDescription = "Select Date",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showDatePicker() }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            /* ---------- GENDER ---------- */
                            ExposedDropdownMenuBox(
                                expanded = isGenderExpanded,
                                onExpandedChange = { isGenderExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = gender,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Gender") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(isGenderExpanded)
                                    },
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )

                                ExposedDropdownMenu(
                                    expanded = isGenderExpanded,
                                    onDismissRequest = { isGenderExpanded = false }
                                ) {
                                    listOf("Male", "Female", "Other").forEach {
                                        DropdownMenuItem(
                                            text = { Text(it) },
                                            onClick = {
                                                gender = it
                                                isGenderExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            /* ---------- BLOOD GROUP ---------- */
                            ExposedDropdownMenuBox(
                                expanded = isBloodExpanded,
                                onExpandedChange = { isBloodExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = bloodGroup,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Blood Group") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(isBloodExpanded)
                                    },
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )

                                ExposedDropdownMenu(
                                    expanded = isBloodExpanded,
                                    onDismissRequest = { isBloodExpanded = false }
                                ) {
                                    listOf(
                                        "A+",
                                        "A-",
                                        "B+",
                                        "B-",
                                        "O+",
                                        "O-",
                                        "AB+",
                                        "AB-"
                                    ).forEach {
                                        DropdownMenuItem(
                                            text = { Text(it) },
                                            onClick = {
                                                bloodGroup = it
                                                isBloodExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = height,
                                    onValueChange = { height = it },
                                    label = { Text("Height (cm)") },
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                OutlinedTextField(
                                    value = weight,
                                    onValueChange = { weight = it },
                                    label = { Text("Weight (kg)") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            OutlinedTextField(
                                value = address,
                                onValueChange = { address = it },
                                label = { Text("Address") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = city,
                                    onValueChange = { city = it },
                                    label = { Text("City") },
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                OutlinedTextField(
                                    value = state,
                                    onValueChange = { state = it },
                                    label = { Text("State") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            OutlinedTextField(
                                value = pin,
                                onValueChange = { pin = it },
                                label = { Text("PIN Code") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(22.dp))

                            Button(
                                onClick = {
                                    if (fullName.isBlank()) {
                                        Toast.makeText(
                                            ctx,
                                            "Full name is required",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@Button
                                    }

                                    scope.launch {
                                        saveProfile(
                                            fullName.trim(),
                                            email.trim(),
                                            phone.trim(),
                                            if (dob.isNotEmpty()) dob else "",
                                            gender,
                                            if (bloodGroup.isNotEmpty()) bloodGroup else "",
                                            if (height.isNotEmpty()) height else "",
                                            if (weight.isNotEmpty()) weight else "",
                                            if (address.isNotEmpty()) address else "",
                                            if (city.isNotEmpty()) city else "",
                                            if (state.isNotEmpty()) state else "",
                                            if (pin.isNotEmpty()) pin else "",
                                            client,
                                            scope
                                        ) { success, message ->
                                            if (success) {
                                                Toast.makeText(
                                                    ctx,
                                                    message ?: "Profile updated successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                // Reload profile data after successful save
                                                scope.launch {
                                                    try {
                                                        val profileResponse =
                                                            RetrofitClient.api.getProfile(email)
                                                        if (profileResponse.success) {
                                                            val data = profileResponse.data
                                                            fullName =
                                                                data.full_name.takeIf { it.isNotEmpty() }
                                                                    ?: fullName
                                                            dob = data.dob ?: ""
                                                            gender = data.gender ?: "Male"
                                                            bloodGroup = data.blood_group ?: ""
                                                            height = data.height_cm ?: ""
                                                            weight = data.weight_kg ?: ""
                                                            address = data.address ?: ""
                                                            city = data.city ?: ""
                                                            state = data.state ?: ""
                                                            pin = data.pin ?: ""
                                                        }
                                                    } catch (e: Exception) {
                                                        android.util.Log.e(
                                                            "EditProfile",
                                                            "Error reloading profile: ${e.message}"
                                                        )
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(
                                                    ctx,
                                                    message ?: "Failed to update profile",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Save Changes")
                            }
                        }
                    }
                }
            }
        }
    }
}

/* ---------------- API FUNCTIONS ---------------- */

private fun saveProfile(
    fullName: String,
    email: String,
    mobile: String,
    dob: String,
    gender: String,
    bloodGroup: String,
    height: String,
    weight: String,
    address: String,
    city: String,
    state: String,
    pin: String,
    client: OkHttpClient,
    scope: kotlinx.coroutines.CoroutineScope,
    onResult: (Boolean, String?) -> Unit
) {
    val json = JSONObject().apply {
        put("full_name", fullName)
        put("email", email)
        put("mobile", mobile)
        if (dob.isNotEmpty()) {
            put("dob", dob)
        }
        if (gender.isNotEmpty()) {
            put("gender", gender)
        }
        if (bloodGroup.isNotEmpty()) {
            put("blood_group", bloodGroup)
        }
        if (height.isNotEmpty()) {
            put("height_cm", height) // Fixed: Changed from "height" to "height_cm"
        }
        if (weight.isNotEmpty()) {
            put("weight_kg", weight) // Fixed: Changed from "weight" to "weight_kg"
        }
        if (address.isNotEmpty()) {
            put("address", address)
        }
        if (city.isNotEmpty()) {
            put("city", city)
        }
        if (state.isNotEmpty()) {
            put("state", state)
        }
        if (pin.isNotEmpty()) {
            put("pin", pin)
        }
    }

    val body = json.toString().toRequestBody("application/json".toMediaType())

    val request = Request.Builder()
        .url("http://10.26.77.190/vaidyacare/api/patientSave_profile.php")
        .post(body)
        .addHeader("Content-Type", "application/json")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            scope.launch {
                android.util.Log.e("EditProfile", "Network error: ${e.message}", e)
                onResult(false, "Network error: ${e.localizedMessage ?: "Please check your connection"}")
            }
        }

        override fun onResponse(call: Call, response: Response) {
            val rawBody = response.body?.string().orEmpty()
            scope.launch {
                try {
                    android.util.Log.d("EditProfile", "Response code: ${response.code}")
                    android.util.Log.d("EditProfile", "Raw response body: $rawBody")
                    android.util.Log.d("EditProfile", "Request URL: http://10.26.77.190/vaidyacare/api/patientSave_profile.php")
                    android.util.Log.d("EditProfile", "Request body: ${json.toString()}")
                    
                    // Strip HTML tags and extract JSON
                    val cleanBody = rawBody
                        .replace(Regex("<[^>]*>"), "") // Remove HTML tags
                        .replace(Regex("\\s+"), " ") // Normalize whitespace
                        .trim()
                    
                    // Try to find JSON object in the response
                    val jsonStart = cleanBody.indexOf("{")
                    val jsonEnd = cleanBody.lastIndexOf("}")
                    val jsonBody = if (jsonStart >= 0 && jsonEnd > jsonStart) {
                        cleanBody.substring(jsonStart, jsonEnd + 1)
                    } else {
                        cleanBody
                    }
                    
                    android.util.Log.d("EditProfile", "Cleaned JSON body: $jsonBody")
                    
                    if (response.code == 200 && jsonBody.isNotEmpty()) {
                        try {
                            val json = JSONObject(jsonBody)
                            val success = json.optBoolean("success", false)
                            val message = json.optString("message", if (success) "Profile updated successfully" else "Failed to update profile")
                            onResult(success, message)
                        } catch (jsonError: Exception) {
                            android.util.Log.e("EditProfile", "JSON parsing error: ${jsonError.message}", jsonError)
                            // Try to extract error message from HTML
                            val errorMsg = if (rawBody.contains("message")) {
                                try {
                                    val extracted = rawBody.substringAfter("\"message\":\"").substringBefore("\"")
                                    extracted.ifEmpty { "Failed to parse server response" }
                                } catch (_: Exception) {
                                    "Server returned invalid response format"
                                }
                            } else {
                                "Server returned invalid response format"
                            }
                            onResult(false, errorMsg)
                        }
                    } else {
                        val errorMsg = if (jsonBody.isNotEmpty()) {
                            try {
                                val json = JSONObject(jsonBody)
                                json.optString("message", "Failed to save. Error code: ${response.code}")
                            } catch (_: Exception) {
                                // Try to extract message from raw body
                                val extracted = rawBody.substringAfter("message").substringBefore("\n").trim()
                                if (extracted.isNotEmpty()) extracted else "Failed to save. Error code: ${response.code}"
                            }
                        } else {
                            "Failed to save. Error code: ${response.code}"
                        }
                        onResult(false, errorMsg)
                    }
                } catch (e: Exception) {
                    android.util.Log.e("EditProfile", "Error processing response: ${e.message}", e)
                    android.util.Log.e("EditProfile", "Raw response body was: $rawBody")
                    onResult(false, "Error processing response: ${e.localizedMessage ?: "Please try again"}")
                }
            }
        }
    })
}
