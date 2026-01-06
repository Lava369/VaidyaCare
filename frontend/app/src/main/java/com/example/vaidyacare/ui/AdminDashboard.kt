@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.annotations.SerializedName
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/* ---------------- API ---------------- */

interface AdminApi {

    @GET("get_pending_doctors.php")
    fun getPendingDoctors(): Call<DoctorListResponse>

    @FormUrlEncoded
    @POST("approve_doctor.php")
    fun approveDoctor(
        @Field("doctor_id") doctorId: Int
    ): Call<CommonResponse>

    @FormUrlEncoded
    @POST("reject_doctor.php")
    fun rejectDoctor(
        @Field("doctor_id") doctorId: Int
    ): Call<CommonResponse>
}

/* ---------------- MODELS ---------------- */

data class DoctorListResponse(
    val success: Boolean,
    val data: List<DoctorApiModel> = emptyList()
)

data class DoctorApiModel(

    @SerializedName("doctor_id")
    val doctorId: Int,

    @SerializedName("full_name")
    val name: String,

    @SerializedName("specialization")
    val specialization: String,

    @SerializedName("experience_years")
    val experience: Int,

    @SerializedName("clinic_name")
    val clinic: String,

    @SerializedName("registration_number")
    val regNo: String
)

data class CommonResponse(
    val success: Boolean,
    val message: String
)

/* ---------------- RETROFIT ---------------- */

private val adminApi: AdminApi = Retrofit.Builder()
    .baseUrl("http://10.26.77.190/vaidyacare/api/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(AdminApi::class.java)

/* ---------------- SCREEN ---------------- */

@Composable
fun AdminDashboard(navController: NavController) {

    val doctors = remember { mutableStateListOf<DoctorApiModel>() }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        val call = adminApi.getPendingDoctors()

        call.enqueue(object : Callback<DoctorListResponse> {

            override fun onResponse(
                call: Call<DoctorListResponse>,
                response: Response<DoctorListResponse>
            ) {
                loading = false
                if (response.isSuccessful && response.body()?.success == true) {
                    doctors.clear()
                    doctors.addAll(response.body()!!.data)
                    error = null
                } else {
                    error = response.errorBody()?.string()
                        ?: "No pending doctor verifications"
                }
            }

            override fun onFailure(call: Call<DoctorListResponse>, t: Throwable) {
                loading = false
                error = t.message ?: "Server error"
            }
        })

        onDispose {
            call.cancel()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("role") {
                            popUpTo("adminDashboard") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF9FAFB))
        ) {

            when {
                loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                error != null -> Text(
                    text = error!!,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )

                doctors.isEmpty() -> Text(
                    "No pending doctor verifications",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(doctors, key = { it.doctorId }) { doctor ->
                        DoctorCard(
                            doctor = doctor,
                            onApprove = {
                                adminApi.approveDoctor(doctor.doctorId)
                                    .enqueue(simpleRemoveCallback(doctors, doctor))
                            },
                            onReject = {
                                adminApi.rejectDoctor(doctor.doctorId)
                                    .enqueue(simpleRemoveCallback(doctors, doctor))
                            }
                        )
                    }
                }
            }
        }
    }
}

/* ---------------- CARD ---------------- */

@Composable
fun DoctorCard(
    doctor: DoctorApiModel,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    var actionLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFEFF6FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null)
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(doctor.name, fontWeight = FontWeight.Bold)
                Text(doctor.specialization, fontSize = 12.sp, color = Color.Gray)
            }
        }

        Spacer(Modifier.height(8.dp))

        Text("Reg No: ${doctor.regNo}", fontSize = 12.sp)
        Text("Experience: ${doctor.experience} years", fontSize = 12.sp)
        Text("Clinic: ${doctor.clinic}", fontSize = 12.sp)

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                modifier = Modifier.weight(1f),
                enabled = !actionLoading,
                onClick = {
                    actionLoading = true
                    onApprove()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
            ) {
                Icon(Icons.Default.Check, null)
                Spacer(Modifier.width(4.dp))
                Text("Approve")
            }

            Button(
                modifier = Modifier.weight(1f),
                enabled = !actionLoading,
                onClick = {
                    actionLoading = true
                    onReject()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
            ) {
                Icon(Icons.Default.Close, null)
                Spacer(Modifier.width(4.dp))
                Text("Reject")
            }
        }
    }
}

/* ---------------- HELPERS ---------------- */

private fun simpleRemoveCallback(
    list: MutableList<DoctorApiModel>,
    item: DoctorApiModel
): Callback<CommonResponse> =
    object : Callback<CommonResponse> {
        override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
            if (response.isSuccessful && response.body()?.success == true) {
                list.remove(item)
            }
        }
        override fun onFailure(call: Call<CommonResponse>, t: Throwable) {}
    }
