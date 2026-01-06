@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("PropertyName")

package com.example.vaidyacare.ui

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileOutputStream

/* ---------------- API RESPONSE ---------------- */

data class ApiResponse(
    val success: Boolean,
    val message: String
)

/* ---------------- API SERVICE ---------------- */

interface DoctorApiService {

    @Multipart
    @POST("doctor_signup.php")
    suspend fun signupDoctor(
        @Part("full_name") fullName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("mobile") mobile: RequestBody,
        @Part("license_no") licenseNo: RequestBody,
        @Part("password") password: RequestBody,

        @Part license_cert: MultipartBody.Part,
        @Part degree_cert: MultipartBody.Part,
        @Part id_proof: MultipartBody.Part
    ): ApiResponse
}

/* ---------------- RETROFIT ---------------- */

object DoctorRetrofit {
    private const val BASE_URL = "http://10.26.77.190/vaidyacare/api/"

    // Create lenient Gson parser to handle malformed JSON responses
    private val lenientGson = GsonBuilder()
        .setLenient()
        .create()

    val api: DoctorApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(lenientGson))
            .build()
            .create(DoctorApiService::class.java)
    }
}

/* ---------------- HELPERS ---------------- */

private fun uriToFile(context: Context, uri: Uri): File {
    val name = context.contentResolver.query(uri, null, null, null, null)?.use {
        it.moveToFirst()
        it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
    } ?: "document"

    val file = File(context.cacheDir, name)

    context.contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }
    return file
}

private fun getFileName(context: Context, uri: Uri): String {
    return context.contentResolver.query(uri, null, null, null, null)?.use {
        it.moveToFirst()
        it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
    } ?: "Selected file"
}

private fun getMediaType(fileName: String): String {
    return when {
        fileName.endsWith(".pdf", ignoreCase = true) -> "application/pdf"
        fileName.endsWith(".jpg", ignoreCase = true) || fileName.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
        fileName.endsWith(".png", ignoreCase = true) -> "image/png"
        else -> "application/octet-stream"
    }
}

/* ---------------- UI ---------------- */

@Composable
fun DoctorSignupScreen(
    onBack: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var licenseNo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreeTerms by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    var licenseCert by remember { mutableStateOf<Uri?>(null) }
    var degreeCert by remember { mutableStateOf<Uri?>(null) }
    var idProof by remember { mutableStateOf<Uri?>(null) }

    var licenseName by remember { mutableStateOf("") }
    var degreeName by remember { mutableStateOf("") }
    var idName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Doctor Sign Up") },
                navigationIcon = {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        null,
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
                .background(Brush.verticalGradient(listOf(Color(0xFFF0F5FF), Color.White)))
                .padding(padding)
                .padding(22.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text("Create Doctor Account", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(Modifier.padding(20.dp)) {

                    OutlinedTextField(fullName, { fullName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(mobile, { mobile = it }, label = { Text("Mobile") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(licenseNo, { licenseNo = it }, label = { Text("Medical License No") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(password, { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(confirmPassword, { confirmPassword = it }, label = { Text("Confirm Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())

                    DocumentUpload("Medical License Certificate", licenseName) {
                        licenseCert = it
                        licenseName = getFileName(context, it)
                    }

                    DocumentUpload("Degree Certificate", degreeName) {
                        degreeCert = it
                        degreeName = getFileName(context, it)
                    }

                    DocumentUpload("ID Proof", idName) {
                        idProof = it
                        idName = getFileName(context, it)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(agreeTerms, { agreeTerms = it })
                        Text("I agree to Terms & Privacy Policy")
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !loading,
                onClick = {
                    // Validation
                    when {
                        !agreeTerms -> {
                            Toast.makeText(context, "Please agree to Terms & Privacy Policy", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        fullName.isBlank() -> {
                            Toast.makeText(context, "Full name is required", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        email.isBlank() || !email.contains("@") -> {
                            Toast.makeText(context, "Valid email is required", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        mobile.isBlank() || mobile.length < 10 -> {
                            Toast.makeText(context, "Valid mobile number is required", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        licenseNo.isBlank() -> {
                            Toast.makeText(context, "Medical license number is required", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        password.length < 6 -> {
                            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        password != confirmPassword -> {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        licenseCert == null -> {
                            Toast.makeText(context, "Please upload Medical License Certificate", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        degreeCert == null -> {
                            Toast.makeText(context, "Please upload Degree Certificate", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        idProof == null -> {
                            Toast.makeText(context, "Please upload ID Proof", Toast.LENGTH_SHORT).show()
                        return@Button
                        }
                    }

                    scope.launch {
                        loading = true

                        try {
                        val response = DoctorRetrofit.api.signupDoctor(
                            RequestBody.create("text/plain".toMediaTypeOrNull(), fullName),
                            RequestBody.create("text/plain".toMediaTypeOrNull(), email),
                            RequestBody.create("text/plain".toMediaTypeOrNull(), mobile),
                            RequestBody.create("text/plain".toMediaTypeOrNull(), licenseNo),
                            RequestBody.create("text/plain".toMediaTypeOrNull(), password),

                            MultipartBody.Part.createFormData(
                                "license_cert",
                                licenseName,
                                    uriToFile(context, licenseCert!!).asRequestBody(getMediaType(licenseName).toMediaTypeOrNull())
                            ),
                            MultipartBody.Part.createFormData(
                                "degree_cert",
                                degreeName,
                                    uriToFile(context, degreeCert!!).asRequestBody(getMediaType(degreeName).toMediaTypeOrNull())
                            ),
                            MultipartBody.Part.createFormData(
                                "id_proof",
                                idName,
                                    uriToFile(context, idProof!!).asRequestBody(getMediaType(idName).toMediaTypeOrNull())
                            )
                        )

                        Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                            
                            if (response.success) {
                                // Don't navigate to login - stay on screen to show success message
                                // User needs to wait for admin approval
                            }
                         } catch (e: Exception) {
                             val errorMsg = when {
                                 e.message?.contains("JsonReader", ignoreCase = true) == true ||
                                 e.message?.contains("setLenient", ignoreCase = true) == true ||
                                 e.message?.contains("Expected BEGIN_OBJECT", ignoreCase = true) == true ->
                                     "Server returned invalid response. Please try again."
                                 
                                 e.message?.contains("Unable to resolve host", ignoreCase = true) == true -> 
                                     "No internet connection. Please check your network."
                                 
                                 e.message?.contains("timeout", ignoreCase = true) == true -> 
                                     "Request timed out. Please try again."
                                 
                                 e.message?.contains("Failed to connect", ignoreCase = true) == true ->
                                     "Cannot connect to server. Please check your connection."
                                 
                                 else -> 
                                     "Registration failed: ${e.localizedMessage ?: "Please try again"}"
                             }
                             Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                         } finally {
                        loading = false
                         }
                    }
                }
            ) {
                if (loading) CircularProgressIndicator(color = Color.White)
                else Text("Create Doctor Account")
            }
        }
    }
}

/* ---------------- DOCUMENT PICKER ---------------- */

@Composable
private fun DocumentUpload(
    title: String,
    fileName: String,
    onSelected: (Uri) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) onSelected(uri)
    }

    Column {
        OutlinedButton(
            onClick = { launcher.launch("*/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.UploadFile, null)
            Spacer(Modifier.width(8.dp))
            Text(title)
        }

        if (fileName.isNotBlank()) {
            Text(
                text = fileName,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}
