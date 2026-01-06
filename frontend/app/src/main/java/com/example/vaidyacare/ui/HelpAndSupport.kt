package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpAndSupportScreen(navController: NavController? = null) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    var faq1 by remember { mutableStateOf(false) }
    var faq2 by remember { mutableStateOf(false) }
    var faq3 by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Support") },
                navigationIcon = {
                    IconButton(onClick = { navController?.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ---------- SUPPORT CARDS ----------
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    SupportCard("Call Us", "24/7 Support", Icons.Default.Phone)
                    SupportCard("Live Chat", "Quick Response", Icons.Default.Phone) // fallback icon
                }

                Spacer(Modifier.height(12.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    SupportCard("Email", "support@vaidyacare.com", Icons.Default.Email)
                    SupportCard("User Guide", "Learn More", Icons.Default.Info)
                }
            }

            // ---------- FAQ ----------
            item {
                Text(
                    "Frequently Asked Questions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                FAQItem(
                    question = "How do I book an appointment?",
                    answer = "Go to Book Appointment and select your preferred doctor & time.",
                    expanded = faq1,
                    onToggle = { faq1 = !faq1 }
                )

                FAQItem(
                    question = "Can I cancel my appointment?",
                    answer = "Yes, you can cancel up to 2 hours before your appointment.",
                    expanded = faq2,
                    onToggle = { faq2 = !faq2 }
                )

                FAQItem(
                    question = "How do I access my medical reports?",
                    answer = "Go to My Reports section to view and download your reports.",
                    expanded = faq3,
                    onToggle = { faq3 = !faq3 }
                )
            }

            // ---------- CONTACT INFO ----------
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEAF3FF), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {

                    Column {
                        Text("Contact Information", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                        Spacer(Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("+91 1800 123 4567")
                        }

                        Spacer(Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("support@vaidyacare.com")
                        }

                        Spacer(Modifier.height(8.dp))
                        Text("Our support team is available 24/7 to assist you.")
                    }
                }
            }

            // ---------- SEND MESSAGE ----------
            item {
                Text("Send us a message", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Your Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Your Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = { Text("Your Message") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Send Message")
                }
            }
        }
    }
}

@Composable
fun SupportCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {

        Icon(icon, contentDescription = null, tint = Color(0xFF1A73E8))
        Spacer(Modifier.height(10.dp))

        Text(title, fontWeight = FontWeight.Bold)
        Text(subtitle, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun FAQItem(question: String, answer: String, expanded: Boolean, onToggle: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 8.dp)
    ) {

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(question, fontWeight = FontWeight.SemiBold)

            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )
        }

        if (expanded) {
            Text(
                answer, fontSize = 13.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
