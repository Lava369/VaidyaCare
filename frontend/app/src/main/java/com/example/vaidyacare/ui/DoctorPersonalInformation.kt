
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import coil.compose.rememberAsyncImagePainter
import com.example.vaidyacare.utils.DoctorSession
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/* ---------------- MAIN SCREEN ---------------- */

@Composable
fun DoctorPersonalInformationScreen(
    onBack: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val client = remember { OkHttpClient() }
    
    // Get doctor_id from session
    val doctorId = remember { DoctorSession.getDoctorId(context) }
    
    var loading by remember { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }
    
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    var selectedMonth by remember { mutableStateOf("January") }
    var monthExpanded by remember { mutableStateOf(false) }

    var gender by remember { mutableStateOf("Male") }
    var genderExpanded by remember { mutableStateOf(false) }
    
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var uploadingImage by remember { mutableStateOf(false) }
    var showImagePickerDialog by remember { mutableStateOf(false) }
    
    // Create temporary file for camera - use unique name each time
    var tempImageFile by remember { mutableStateOf<File?>(null) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    
    // Function to create camera URI
    fun createCameraUri(): Uri? {
        return try {
            val file = File(context.cacheDir, "temp_profile_${System.currentTimeMillis()}.jpg")
            // Ensure parent directory exists
            file.parentFile?.mkdirs()
            tempImageFile = file
            
            // Use FileProvider URI for camera (works on all Android versions)
            try {
                androidx.core.content.FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } catch (e: Exception) {
                // Fallback to file URI if FileProvider not configured
                Uri.fromFile(file)
            }
        } catch (e: Exception) {
            null
        }
    }
    
    // Initialize camera URI
    LaunchedEffect(Unit) {
        cameraImageUri = createCameraUri()
    }
    
    // Image picker launchers
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profileImageUri = it
            uploadingImage = true
            uploadProfileImage(doctorId, it, context, client, scope) { success, imageUrl ->
                uploadingImage = false
                if (success) {
                    profileImageUrl = imageUrl
                    Toast.makeText(context, "Profile image uploaded successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // The photo was saved to tempImageFile
            tempImageFile?.let { file ->
                if (file.exists()) {
                    // Get URI for the saved file
                    val photoUri = try {
                        androidx.core.content.FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                    } catch (e: Exception) {
                        Uri.fromFile(file)
                    }
                    
                    profileImageUri = photoUri
                    uploadingImage = true
                    uploadProfileImage(doctorId, photoUri, context, client, scope) { uploadSuccess, imageUrl ->
                        uploadingImage = false
                        if (uploadSuccess) {
                            profileImageUrl = imageUrl
                            Toast.makeText(context, "Profile image uploaded successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                        }
                    }
                    
                    // Prepare for next camera capture
                    cameraImageUri = createCameraUri()
                }
            }
        } else {
            Toast.makeText(context, "Photo capture cancelled", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Load personal information
    LaunchedEffect(doctorId) {
        if (doctorId > 0) {
            loadPersonalInfo(doctorId, client, scope) { data ->
                fullName = data["full_name"] ?: ""
                email = data["email"] ?: ""
                phone = data["phone"] ?: ""
                location = data["location"] ?: ""
                selectedMonth = data["birth_month"] ?: "January"
                gender = data["gender"] ?: "Male"
                profileImageUrl = data["profile_image"]?.takeIf { it.isNotEmpty() }
                loading = false
            }
        } else {
            loading = false
        }
    }

    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Personal Information", fontWeight = FontWeight.Bold)
                        Text(
                            "Update your personal details",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF22C55E),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            /* -------- PROFILE -------- */

            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF22C55E), Color(0xFF16A34A))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uploadingImage) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(30.dp),
                                color = Color.White
                            )
                        } else if (profileImageUri != null || profileImageUrl != null) {
                            val imageUri = profileImageUri ?: Uri.parse("http://10.26.77.190${profileImageUrl}")
                            Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = if (fullName.isNotEmpty()) {
                                    fullName.split(" ").take(2).joinToString("") { it.firstOrNull()?.toString() ?: "" }.uppercase()
                                } else "AK",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text("Profile Picture", fontWeight = FontWeight.Bold)
                        Text(
                            "Change Photo",
                            color = Color(0xFF22C55E),
                            modifier = Modifier.clickable {
                                showImagePickerDialog = true
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (!loading) {
                FormField("Full Name *", fullName) { fullName = it }
                FormField("Email Address *", email) { email = it }
                FormField("Phone Number *", phone) { phone = it }
                FormField("Location *", location) { location = it }
            }

            if (!loading) {
                /* -------- MONTH DROPDOWN (ALL MONTHS) -------- */

                ExposedDropdownMenuBox(
                expanded = monthExpanded,
                onExpandedChange = { monthExpanded = !monthExpanded }
            ) {
                OutlinedTextField(
                    value = selectedMonth,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Birth Month") },
                    trailingIcon = {
                        Icon(Icons.Filled.DateRange, contentDescription = null)
                    },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                ExposedDropdownMenu(
                    expanded = monthExpanded,
                    onDismissRequest = { monthExpanded = false }
                ) {
                    months.forEach { month ->
                        DropdownMenuItem(
                            text = { Text(month) },
                            onClick = {
                                selectedMonth = month
                                monthExpanded = false
                            }
                        )
                    }
                }
            }

            /* -------- GENDER -------- */

            ExposedDropdownMenuBox(
                expanded = genderExpanded,
                onExpandedChange = { genderExpanded = !genderExpanded }
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Gender") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(genderExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                ExposedDropdownMenu(
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

            Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Image Picker Dialog
            if (showImagePickerDialog) {
                AlertDialog(
                    onDismissRequest = { showImagePickerDialog = false },
                    title = { Text("Select Photo", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    showImagePickerDialog = false
                                    // Create fresh URI for camera
                                    val uri = createCameraUri()
                                    if (uri != null) {
                                        cameraImageUri = uri
                                        try {
                                            cameraLauncher.launch(uri)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Camera not available: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Camera not ready. Please try again.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF22C55E)
                                )
                            ) {
                                Icon(Icons.Filled.CameraAlt, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Take Photo")
                            }
                            
                            Button(
                                onClick = {
                                    showImagePickerDialog = false
                                    galleryLauncher.launch("image/*")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF22C55E)
                                )
                            ) {
                                Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Choose from Gallery")
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showImagePickerDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            Button(
                onClick = {
                    if (doctorId <= 0) {
                        Toast.makeText(context, "Invalid Doctor ID. Please login again.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    scope.launch {
                        saving = true
                        savePersonalInfo(
                            doctorId,
                            fullName,
                            email,
                            phone,
                            location,
                            selectedMonth,
                            gender,
                            client,
                            scope
                        ) { success, message ->
                            saving = false
                            if (success) {
                                Toast.makeText(context, "Personal information saved successfully!", Toast.LENGTH_SHORT).show()
                                onSave()
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                enabled = !loading && !saving && doctorId > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF22C55E)
                )
            ) {
                if (saving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            }
            
            if (loading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

/* ---------------- REUSABLE FIELD ---------------- */

@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

/* ---------------- API FUNCTIONS ---------------- */

private fun loadPersonalInfo(
    doctorId: Int,
    client: OkHttpClient,
    scope: kotlinx.coroutines.CoroutineScope,
    onResult: (Map<String, String>) -> Unit
) {
    val request = Request.Builder()
        .url("http://10.26.77.190/vaidyacare/api/get_doctor_personal_info.php?doctor_id=$doctorId")
        .get()
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            scope.launch {
                onResult(emptyMap())
            }
        }

        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string().orEmpty()
            scope.launch {
                try {
                    val json = JSONObject(body)
                    if (json.optBoolean("success")) {
                        val data = json.getJSONObject("data")
                        onResult(mapOf(
                            "full_name" to data.optString("full_name", ""),
                            "email" to data.optString("email", ""),
                            "phone" to data.optString("phone", ""),
                            "location" to data.optString("location", ""),
                            "birth_month" to data.optString("birth_month", "January"),
                            "gender" to data.optString("gender", "Male"),
                            "profile_image" to data.optString("profile_image", "")
                        ))
                    } else {
                        onResult(emptyMap())
                    }
                } catch (e: Exception) {
                    onResult(emptyMap())
                }
            }
        }
    })
}

private fun savePersonalInfo(
    doctorId: Int,
    fullName: String,
    email: String,
    phone: String,
    location: String,
    birthMonth: String,
    gender: String,
    client: OkHttpClient,
    scope: kotlinx.coroutines.CoroutineScope,
    onResult: (Boolean, String) -> Unit
) {
    val json = JSONObject().apply {
        put("doctor_id", doctorId)
        put("full_name", fullName)
        put("email", email)
        put("phone", phone)
        put("location", location)
        put("birth_month", birthMonth)
        put("gender", gender)
    }

    val body = json.toString().toRequestBody("application/json".toMediaType())

    val request = Request.Builder()
        .url("http://10.26.77.190/vaidyacare/api/update_doctor_personal_info.php")
        .post(body)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            scope.launch {
                onResult(false, "Network error: ${e.message}")
            }
        }

        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string().orEmpty()
            scope.launch {
                try {
                    if (response.code == 200 && body.isNotEmpty()) {
                        val json = JSONObject(body)
                        val success = json.optBoolean("success")
                        val message = json.optString("message", if (success) "Saved successfully" else "Failed to save")
                        onResult(success, message)
                    } else {
                        val errorMsg = if (body.isNotEmpty()) {
                            try {
                                val json = JSONObject(body)
                                json.optString("message", "Failed to save. Error code: ${response.code}")
                            } catch (_: Exception) {
                                "Failed to save. Error code: ${response.code}"
                            }
                        } else {
                            "Failed to save. Error code: ${response.code}"
                        }
                        onResult(false, errorMsg)
                    }
                } catch (e: Exception) {
                    onResult(false, "Error parsing response: ${e.localizedMessage ?: "Please try again"}")
                }
            }
        }
    })
}

private fun uploadProfileImage(
    doctorId: Int,
    imageUri: Uri,
    context: android.content.Context,
    client: OkHttpClient,
    scope: kotlinx.coroutines.CoroutineScope,
    onResult: (Boolean, String?) -> Unit
) {
    scope.launch {
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri) ?: return@launch
            val file = File(context.cacheDir, "profile_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
            inputStream.close()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("doctor_id", doctorId.toString())
                .addFormDataPart(
                    "profile_image",
                    file.name,
                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                )
                .build()

            val request = Request.Builder()
                .url("http://10.26.77.190/vaidyacare/api/upload_doctor_profile_image.php")
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    scope.launch {
                        onResult(false, null)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string().orEmpty()
                    scope.launch {
                        try {
                            val json = JSONObject(body)
                            val success = json.optBoolean("success")
                            if (success) {
                                val data = json.optJSONObject("data")
                                val imageUrl = data?.optString("image_url", null)
                                onResult(true, imageUrl)
                            } else {
                                onResult(false, null)
                            }
                        } catch (e: Exception) {
                            onResult(false, null)
                        }
                    }
                }
            })
        } catch (e: Exception) {
            scope.launch {
                onResult(false, null)
            }
        }
    }
}
