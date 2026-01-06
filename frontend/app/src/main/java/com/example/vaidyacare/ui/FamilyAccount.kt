@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/* ---------------- API CONFIG ---------------- */

private const val BASE_URL = "http://10.26.77.190/vaidyacare/api/family/"
private const val USER_ID = 1
private val client = OkHttpClient()

/* ---------------- DATA MODEL ---------------- */

data class FamilyMember(
    val id: Int,
    val name: String,
    val relation: String,
    val age: Int,
    val gender: String,
    val phone: String,
    val bloodGroup: String,
    val address: String = "",
    val medicalHistory: String = "",
    val allergies: String = "",
    val medications: String = ""
)

/* ---------------- ROOT ---------------- */

@Composable
fun FamilyAccount() {

    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val members = remember { mutableStateListOf<FamilyMember>() }

    LaunchedEffect(Unit) {
        scope.launch {
            val req = Request.Builder()
                .url("${BASE_URL}get_members.php?user_id=$USER_ID")
                .build()

            val res = withContext(Dispatchers.IO) {
                client.newCall(req).execute().body?.string()
            } ?: return@launch

            try {
                val json = JSONObject(res)
                if (json.optBoolean("success", false)) {
                    val arr = json.getJSONArray("data")
                    members.clear()

                    for (i in 0 until arr.length()) {
                        val o = arr.getJSONObject(i)
                        members.add(
                            FamilyMember(
                                id = o.getInt("id"),
                                name = o.getString("name"),
                                relation = o.getString("relation"),
                                age = o.getInt("age"),
                                gender = o.optString("gender", ""),
                                phone = o.optString("phone", ""),
                                bloodGroup = o.optString("blood_group", ""),
                                address = o.optString("address", ""),
                                medicalHistory = o.optString("medical_history", ""),
                                allergies = o.optString("allergies", ""),
                                medications = o.optString("medications", "")
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("FamilyAccount", "Error parsing response: ${e.message}", e)
            }
        }
    }

    NavHost(navController, startDestination = "list") {

        composable("list") {
            FamilyListScreen(
                members = members,
                onAdd = { navController.navigate("add") },
                onEdit = { id: Int -> navController.navigate("edit/$id") },
                onDelete = { id: Int ->
                    scope.launch {
                        val body = JSONObject()
                            .put("id", id)
                            .toString()
                            .toRequestBody("application/json".toMediaType())

                        val req = Request.Builder()
                            .url("${BASE_URL}delete_member.php")
                            .post(body)
                            .build()

                        withContext(Dispatchers.IO) {
                            client.newCall(req).execute()
                        }

                        members.removeIf { it.id == id }
                    }
                }
            )
        }

        composable("add") {
            AddEditFamilyMemberScreen(
                title = "Add Family Member",
                member = null,
                onSave = { member ->
                    scope.launch {

                        val json = JSONObject()
                            .put("user_id", USER_ID)
                            .put("name", member.name)
                            .put("relation", member.relation)
                            .put("age", member.age)
                            .put("gender", member.gender)
                            .put("phone", member.phone)
                            .put("blood_group", member.bloodGroup)
                            .put("address", member.address)
                            .put("medical_history", member.medicalHistory)
                            .put("allergies", member.allergies)
                            .put("medications", member.medications)

                        val body = json.toString()
                            .toRequestBody("application/json".toMediaType())

                        val req = Request.Builder()
                            .url("${BASE_URL}add_member.php")
                            .post(body)
                            .build()

                        val res = withContext(Dispatchers.IO) {
                            client.newCall(req).execute().body?.string()
                        } ?: return@launch

                        try {
                            val obj = JSONObject(res)
                            if (obj.optBoolean("success", false)) {
                                // Reload members to get the actual ID from server
                                val reloadReq = Request.Builder()
                                    .url("${BASE_URL}get_members.php?user_id=$USER_ID")
                                    .build()
                                
                                val reloadRes = withContext(Dispatchers.IO) {
                                    client.newCall(reloadReq).execute().body?.string()
                                }
                                
                                if (reloadRes != null) {
                                    val reloadJson = JSONObject(reloadRes)
                                    if (reloadJson.optBoolean("success", false)) {
                                        val arr = reloadJson.getJSONArray("data")
                                        members.clear()
                                        for (i in 0 until arr.length()) {
                                            val o = arr.getJSONObject(i)
                                            members.add(
                                                FamilyMember(
                                                    id = o.getInt("id"),
                                                    name = o.getString("name"),
                                                    relation = o.getString("relation"),
                                                    age = o.getInt("age"),
                                                    gender = o.optString("gender", ""),
                                                    phone = o.optString("phone", ""),
                                                    bloodGroup = o.optString("blood_group", ""),
                                                    address = o.optString("address", ""),
                                                    medicalHistory = o.optString("medical_history", ""),
                                                    allergies = o.optString("allergies", ""),
                                                    medications = o.optString("medications", "")
                                                )
                                            )
                                        }
                                    }
                                }
                                navController.popBackStack()
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("FamilyAccount", "Error adding member: ${e.message}", e)
                        }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "edit/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("id") ?: 0
            val index = members.indexOfFirst { it.id == id }

            if (index >= 0) {
                AddEditFamilyMemberScreen(
                    title = "Edit Family Member",
                    member = members[index],
                onSave = { member ->
                    scope.launch {

                        val json = JSONObject()
                            .put("id", member.id)
                            .put("name", member.name)
                            .put("relation", member.relation)
                            .put("age", member.age)
                            .put("gender", member.gender)
                            .put("phone", member.phone)
                            .put("blood_group", member.bloodGroup)
                            .put("address", member.address)
                            .put("medical_history", member.medicalHistory)
                            .put("allergies", member.allergies)
                            .put("medications", member.medications)

                        val body = json.toString()
                            .toRequestBody("application/json".toMediaType())

                        val req = Request.Builder()
                            .url("${BASE_URL}update_member.php")
                            .post(body)
                            .build()

                        try {
                            val response = withContext(Dispatchers.IO) {
                                client.newCall(req).execute()
                            }
                            
                            val responseBody = response.body?.string() ?: ""
                            val json = JSONObject(responseBody)
                            
                            if (json.optBoolean("success", false)) {
                                members[index] = member
                                navController.popBackStack()
                            } else {
                                android.util.Log.e("FamilyAccount", "Update failed: ${json.optString("message", "Unknown error")}")
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("FamilyAccount", "Error updating member: ${e.message}", e)
                        }
                    }
                },
                onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

/* ---------------- FAMILY LIST ---------------- */

@Composable
fun FamilyListScreen(
    members: List<FamilyMember>,
    onAdd: () -> Unit,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Family Health Manager") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) { Text("+") }
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            items(members) { member ->
                Card(
                    modifier = Modifier.padding(12.dp).fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(member.name, fontWeight = FontWeight.Bold)
                        Text("${member.relation} • ${member.age} years")
                        Text("Phone: ${member.phone}")

                        Spacer(Modifier.height(12.dp))

                        Row {
                            Button(onClick = { onEdit(member.id) }) { Text("Edit") }
                            Spacer(Modifier.width(8.dp))
                            Button(
                                colors = ButtonDefaults.buttonColors(Color.Red),
                                onClick = { onDelete(member.id) }
                            ) { Text("Delete") }
                        }
                    }
                }
            }
        }
    }
}

/* ---------------- ADD / EDIT SCREEN ---------------- */

@Composable
fun AddEditFamilyMemberScreen(
    title: String,
    member: FamilyMember?,
    readOnly: Boolean = false,
    onSave: (FamilyMember) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf(member?.name ?: "") }
    var relation by remember { mutableStateOf(member?.relation ?: "") }
    var age by remember { mutableStateOf(member?.age?.toString() ?: "") }
    var gender by remember { mutableStateOf(member?.gender ?: "") }
    var phone by remember { mutableStateOf(member?.phone ?: "") }
    var blood by remember { mutableStateOf(member?.bloodGroup ?: "") }
    var address by remember { mutableStateOf(member?.address ?: "") }
    var history by remember { mutableStateOf(member?.medicalHistory ?: "") }
    var allergies by remember { mutableStateOf(member?.allergies ?: "") }
    var meds by remember { mutableStateOf(member?.medications ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            BigField("Full Name", name, readOnly) { name = it }
            BigField("Relation", relation, readOnly) { relation = it }
            BigField("Age", age, readOnly) { age = it }
            BigField("Gender", gender, readOnly) { gender = it }
            BigField("Phone", phone, readOnly) { phone = it }
            BigField("Blood Group", blood, readOnly) { blood = it }
            BigField("Address", address, readOnly, true) { address = it }
            BigField("Medical History", history, readOnly, true) { history = it }
            BigField("Allergies", allergies, readOnly, true) { allergies = it }
            BigField("Medications", meds, readOnly, true) { meds = it }

            if (!readOnly) {
                Spacer(Modifier.height(24.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val safeAge = age.toIntOrNull() ?: 0
                        if (name.isNotBlank() && safeAge > 0) {
                            onSave(
                                FamilyMember(
                                    id = member?.id ?: 0,
                                    name = name,
                                    relation = relation,
                                    age = safeAge,
                                    gender = gender,
                                    phone = phone,
                                    bloodGroup = blood,
                                    address = address,
                                    medicalHistory = history,
                                    allergies = allergies,
                                    medications = meds
                                )
                            )
                        }
                    }
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/* ---------------- INPUT FIELD ---------------- */

@Composable
fun BigField(
    label: String,
    value: String,
    readOnly: Boolean,
    multiLine: Boolean = false,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (!readOnly) onValueChange(it) },
        readOnly = readOnly,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        minLines = if (multiLine) 3 else 1,
        maxLines = if (multiLine) 5 else 1
    )
}
