@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment     // ⭐ FIXED IMPORT
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vaidyacare.R
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@Composable
fun HomeServiceScreen(
    navController: NavController,
    onConfirm: () -> Unit = {}
) {

    /* ---------------- BACKGROUND ---------------- */
    val bgGradient = Brush.verticalGradient(
        listOf(Color(0xFFFF8A00), Color(0xFFFFBB33), Color.White)
    )

    /* ---------------- RECEIVE NURSE FROM NurseSelection ---------------- */
    val selectedFlow: StateFlow<String>? =
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow("selectedNurse", "")

    var nurse by remember { mutableStateOf("") }

    LaunchedEffect(selectedFlow) {
        selectedFlow?.collectLatest {
            if (it.isNotEmpty()) nurse = it
        }
    }

    /* ---------------- FORM STATES ---------------- */
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var patient by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }

    val timeOptions = listOf("08–10 AM", "10–12 PM", "12–2 PM", "2–4 PM")

    /* ---------------- DATE PICKER ---------------- */
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            val d = day.toString().padStart(2, '0')
            val m = (month + 1).toString().padStart(2, '0')
            date = "$m/$d/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    /* ---------------- UI ---------------- */
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Home Service Booking", fontWeight = FontWeight.Bold)
                        Text("Book professional home nursing service", fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFF8A00))
            )
        }
    ) { pad ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(Color.White)
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {

                        /* ---------------- SELECT NURSE (UPDATED) ---------------- */
                        Label("Select Nurse")

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate("nurseSelection") }, // ⭐ WORKING
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(Color.White),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(
                                    text = nurse.ifEmpty { "Choose a verified nurse specialist" }, // ⭐ FIXED
                                    fontSize = 15.sp,
                                    color = if (nurse.isEmpty()) Color.Gray else Color.Black,
                                    fontWeight = if (nurse.isEmpty()) FontWeight.Normal else FontWeight.SemiBold
                                )

                                Icon(
                                    painter = painterResource(id = R.drawable.ic_arrow_down),
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }
                        }

                        /* ---------------- DATE ---------------- */
                        Label("Appointment Date")
                        OutlinedTextField(
                            value = date,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { datePicker.show() },
                            placeholder = { Text("mm/dd/yyyy") },
                            trailingIcon = {
                                IconButton(onClick = { datePicker.show() }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_calendar),
                                        contentDescription = null
                                    )
                                }
                            }
                        )

                        /* ---------------- TIME DROPDOWN ---------------- */
                        Label("Preferred Time")

                        var expanded by remember { mutableStateOf(false) }

                        Box {
                            OutlinedTextField(
                                value = time,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = true },
                                placeholder = { Text("Select time slot") },
                                trailingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_arrow_down),
                                        contentDescription = null
                                    )
                                }
                            )

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                timeOptions.forEach { slot ->
                                    DropdownMenuItem(
                                        text = { Text(slot) },
                                        onClick = {
                                            time = slot
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        /* ---------------- OTHER FIELDS ---------------- */
                        Label("Patient Name")
                        OutlinedTextField(
                            value = patient,
                            onValueChange = { patient = it },
                            placeholder = { Text("Enter patient's full name") }
                        )

                        Label("Full Address")
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            placeholder = { Text("House / Street / Landmark") }
                        )

                        Label("City / Location")
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            placeholder = { Text("Enter city or area") }
                        )

                        Label("Reason for Visit")
                        OutlinedTextField(
                            value = reason,
                            onValueChange = { reason = it },
                            placeholder = { Text("Describe patient's condition…") },
                            modifier = Modifier.height(130.dp)
                        )

                        /* ---------------- CONFIRM BUTTON ---------------- */
                        Button(
                            onClick = onConfirm,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(Color(0xFF10C46A))
                        ) {
                            Text("Confirm Booking", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

/* ---------------- LABEL ---------------- */
@Composable
private fun Label(text: String) {
    Text(text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
}
