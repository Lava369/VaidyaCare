@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import java.text.SimpleDateFormat
import java.util.*

/* ===================================================
   BOOK APPOINTMENT FLOW (CALLED FROM Navigation.kt)
=================================================== */

@Composable
fun BookAppointmentApp(onFinish: () -> Unit = {}) {

    val navController = rememberNavController()

    NavHost(navController, startDestination = "consultation") {

        composable("consultation") {
            ConsultationTypeScreen {
                navController.navigate("date_time")
            }
        }

        composable("date_time") {
            DateTimeScreen(
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate("reason") }
            )
        }

        composable("reason") {
            ReasonForVisitScreen(
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate("payment") }
            )
        }

        composable("payment") {
            PaymentScreen(
                onBack = { navController.popBackStack() },
                onPaySuccess = { navController.navigate("chat") }
            )
        }

        composable("chat") {
            ChatConsultationScreen {
                navController.navigate("followup")
            }
        }

        composable("followup") {
            FreeFollowUpScreen(onFinish)
        }
    }
}

/* ===================================================
   1️⃣ CONSULTATION TYPE
=================================================== */

@Composable
fun ConsultationTypeScreen(onContinue: () -> Unit) {
    var selected by remember { mutableStateOf<String?>(null) }

    Scaffold(topBar = { TopAppBar(title = { Text("Choose Consultation") }) }) { padding ->

        Column(
            Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            listOf(
                "Online Chat Consultation",
                "Voice Call Consultation",
                "Video Consultation"
            ).forEach {
                SelectableCard(it, selected == it) { selected = it }
            }

            Button(
                onClick = onContinue,
                enabled = selected != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue")
            }
        }
    }
}

/* ===================================================
   2️⃣ DATE & TIME (CALENDAR + TIME)
=================================================== */

@Composable
fun DateTimeScreen(onBack: () -> Unit, onContinue: () -> Unit) {

    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }
    var showCalendar by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Date & Time") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedButton(onClick = { showCalendar = true }, modifier = Modifier.fillMaxWidth()) {
                Text(
                    selectedDate?.let {
                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
                    } ?: "Select Date"
                )
            }

            if (showCalendar) {
                DatePickerDialog(
                    onDismissRequest = { showCalendar = false },
                    confirmButton = {
                        TextButton(onClick = {
                            selectedDate = datePickerState.selectedDateMillis
                            showCalendar = false
                        }) { Text("OK") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            if (selectedDate != null) {
                listOf(
                    "09:00 AM", "09:30 AM", "10:00 AM",
                    "11:00 AM", "02:00 PM", "05:00 PM"
                ).forEach {
                    SelectableCard(it, selectedTime == it) { selectedTime = it }
                }
            }

            Button(
                onClick = onContinue,
                enabled = selectedDate != null && selectedTime != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue")
            }
        }
    }
}

/* ===================================================
   3️⃣ REASON FOR VISIT
=================================================== */

@Composable
fun ReasonForVisitScreen(onBack: () -> Unit, onContinue: () -> Unit) {
    var reason by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reason for Visit") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(Modifier.padding(padding).padding(16.dp)) {

            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Describe symptoms") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onContinue,
                enabled = reason.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue")
            }
        }
    }
}

/* ===================================================
   4️⃣ PAYMENT (LIKE IMAGE + REAL UPI)
=================================================== */

@Composable
fun PaymentScreen(onBack: () -> Unit, onPaySuccess: () -> Unit) {

    val context = LocalContext.current
    var selected by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Payment Method") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            PaymentMethodCard("Credit Card", "Visa, Mastercard, RuPay", selected == "CARD") {
                selected = "CARD"
            }

            PaymentMethodCard("Debit Card", "Visa, Mastercard, RuPay", selected == "DEBIT") {
                selected = "DEBIT"
            }

            PaymentMethodCard("UPI", "GPay, PhonePe, Paytm", selected == "UPI") {
                selected = "UPI"
            }

            if (selected == "UPI") {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    UpiButton("GPay") {
                        openUpiApp(context, "com.google.android.apps.nbu.paisa.user")
                        onPaySuccess()
                    }
                    UpiButton("PhonePe") {
                        openUpiApp(context, "com.phonepe.app")
                        onPaySuccess()
                    }
                    UpiButton("Paytm") {
                        openUpiApp(context, "net.one97.paytm")
                        onPaySuccess()
                    }
                }
            }

            Button(
                onClick = onPaySuccess,
                enabled = selected != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pay ₹299 & Continue")
            }
        }
    }
}

/* ===================================================
   5️⃣ CHAT
=================================================== */

@Composable
fun ChatConsultationScreen(onEnd: () -> Unit) {
    var message by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dr. Priya Sharma") },
                actions = {
                    TextButton(onClick = onEnd) { Text("End Chat") }
                }
            )
        },
        bottomBar = {
            Row(
                Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFF1F1F1), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                )
                IconButton(onClick = { message = "" }) {
                    Icon(Icons.Default.Send, null)
                }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Doctor: Hello! How can I help you today?")
        }
    }
}

/* ===================================================
   6️⃣ FREE FOLLOW-UP
=================================================== */

@Composable
fun FreeFollowUpScreen(onFinish: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Free Follow-Up") }) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Free Follow-Up Available", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) {
                Text("Start Free Follow-Up")
            }
        }
    }
}

/* ===================================================
   REUSABLE COMPONENTS
=================================================== */

@Composable
fun SelectableCard(title: String, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFF2F6BFF) else Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            title,
            Modifier.padding(16.dp),
            color = if (selected) Color.White else Color.Black
        )
    }
}

@Composable
fun PaymentMethodCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFFEAF0FF) else Color.White
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(subtitle, fontSize = 13.sp, color = Color.Gray)
        }
    }
}

@Composable
fun UpiButton(name: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.size(90.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(name)
        }
    }
}

/* ===================================================
   REAL UPI INTENT
=================================================== */

fun openUpiApp(context: Context, packageName: String) {
    val uri = Uri.parse("upi://pay?pa=test@upi&pn=VaidyaCare&am=299&cu=INR")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.setPackage(packageName)
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // app not installed
    }
}