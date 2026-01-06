@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/* ----------------------------------------------------
   DATA MODELS
---------------------------------------------------- */

data class PatientMessage(
    val name: String,
    val age: Int,
    val gender: String,
    val lastMessage: String,
    val time: String
)

data class ChatMessage(
    val message: String,
    val isDoctor: Boolean,
    val time: String
)

/* ----------------------------------------------------
   SCREEN STATE (INTERNAL)
---------------------------------------------------- */

enum class MessageScreen {
    LIST, REPLY, HISTORY
}

/* ----------------------------------------------------
   MAIN ENTRY (CONNECTED TO NAVCONTROLLER)
---------------------------------------------------- */

@Composable
fun PatientMessagesScreen(
    navController: NavController
) {
    var currentScreen by remember { mutableStateOf(MessageScreen.LIST) }
    var selectedPatient by remember { mutableStateOf<PatientMessage?>(null) }

    when (currentScreen) {

        MessageScreen.LIST -> {
            PatientMessagesList(
                onBack = { navController.popBackStack() },
                onReplyClick = {
                    selectedPatient = it
                    currentScreen = MessageScreen.REPLY
                },
                onHistoryClick = {
                    selectedPatient = it
                    currentScreen = MessageScreen.HISTORY
                }
            )
        }

        MessageScreen.REPLY -> {
            ReplyScreen(
                patient = selectedPatient!!,
                onBack = { currentScreen = MessageScreen.LIST }
            )
        }

        MessageScreen.HISTORY -> {
            MessageHistoryScreen(
                patient = selectedPatient!!,
                onBack = { currentScreen = MessageScreen.LIST }
            )
        }
    }
}

/* ----------------------------------------------------
   PATIENT MESSAGE LIST (WITH REAL BACK)
---------------------------------------------------- */

@Composable
fun PatientMessagesList(
    onBack: () -> Unit,
    onReplyClick: (PatientMessage) -> Unit,
    onHistoryClick: (PatientMessage) -> Unit
) {

    val patients = listOf(
        PatientMessage("Rajesh Kumar", 45, "Male", "When should I take the medicine?", "2 min ago"),
        PatientMessage("Priya Sharma", 32, "Female", "Thank you for the consultation", "15 min ago"),
        PatientMessage("Amit Patel", 28, "Male", "Can I reschedule tomorrow?", "1 hour ago"),
        PatientMessage("Sneha Reddy", 38, "Female", "Prescription received, thanks!", "2 hours ago"),
        PatientMessage("Vikram Singh", 52, "Male", "Still experiencing symptoms", "3 hours ago")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Patient Messages") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            items(patients) { patient ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF9C27B0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    patient.name.take(1),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column {
                                Text(patient.name, fontWeight = FontWeight.Bold)
                                Text("${patient.age}, ${patient.gender}", fontSize = 12.sp)
                            }

                            Spacer(Modifier.weight(1f))
                            Text(patient.time, fontSize = 11.sp)
                        }

                        Spacer(Modifier.height(8.dp))
                        Text(patient.lastMessage)

                        Spacer(Modifier.height(10.dp))

                        Row {
                            Button(
                                onClick = { onReplyClick(patient) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Reply")
                            }

                            Spacer(Modifier.width(8.dp))

                            OutlinedButton(
                                onClick = { onHistoryClick(patient) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("View History")
                            }
                        }
                    }
                }
            }
        }
    }
}

/* ----------------------------------------------------
   REPLY SCREEN
---------------------------------------------------- */

@Composable
fun ReplyScreen(
    patient: PatientMessage,
    onBack: () -> Unit
) {
    var replyText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reply to ${patient.name}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    placeholder = { Text("Type your reply...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp)
                )

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = { replyText = "" },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF9C27B0), CircleShape)
                ) {
                    Icon(Icons.Default.Send, null, tint = Color.White)
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFEDEDED), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                        .widthIn(max = 260.dp)
                ) {
                    Column {
                        Text(patient.lastMessage)
                        Text(patient.time, fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (replyText.isNotEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF9C27B0), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                            .widthIn(max = 260.dp)
                    ) {
                        Text(replyText, color = Color.White)
                    }
                }
            }
        }
    }
}

/* ----------------------------------------------------
   MESSAGE HISTORY SCREEN
---------------------------------------------------- */

@Composable
fun MessageHistoryScreen(
    patient: PatientMessage,
    onBack: () -> Unit
) {

    val history = listOf(
        ChatMessage("Hello doctor, I got the prescription you sent.", false, "2 days ago"),
        ChatMessage("Great! Follow the dosage carefully.", true, "2 days ago"),
        ChatMessage("Before or after meals?", false, "1 day ago"),
        ChatMessage("After meals to avoid irritation.", true, "1 day ago"),
        ChatMessage("When should I take the medicine?", false, "2 min ago")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${patient.name}'s Message History") },
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
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            history.forEach { msg ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment =
                        if (msg.isDoctor) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (msg.isDoctor) Color(0xFF9C27B0) else Color(0xFFEDEDED),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                            .widthIn(max = 260.dp)
                    ) {
                        Column {
                            Text(
                                msg.message,
                                color = if (msg.isDoctor) Color.White else Color.Black
                            )
                            Text(
                                msg.time,
                                fontSize = 10.sp,
                                color = if (msg.isDoctor) Color.White else Color.Gray
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
