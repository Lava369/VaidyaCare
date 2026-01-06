@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ===================================================
   WRAPPER FOR NAVIGATION
=================================================== */

@Composable
fun CardiologistScreen(
    onBack: () -> Unit = {},
    onBookNow: () -> Unit = {}
) {
    CardiologistBookAppointmentScreen(
        onBack = onBack,
        onBookNow = onBookNow
    )
}

/* ===================================================
   MAIN SCREEN (SCROLL ENABLED)
=================================================== */

@Composable
fun CardiologistBookAppointmentScreen(
    onBack: () -> Unit = {},
    onBookNow: () -> Unit = {}
) {

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Reviews", "Info")
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cardiologist") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = onBookNow,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Book Appointment Now")
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            /* ---------------- TABS (FIXED) ---------------- */
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.padding(8.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            /* ---------------- SCROLLABLE CONTENT ---------------- */
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                when (selectedTab) {
                    0 -> CardiologistOverviewTab()
                    1 -> CardiologistReviewsTab()
                    2 -> CardiologistInfoTab()
                }

                Spacer(modifier = Modifier.height(100.dp)) // space above bottom button
            }
        }
    }
}

/* ===================================================
   OVERVIEW TAB
=================================================== */

@Composable
fun CardiologistOverviewTab() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text("Dr. Priya Sharma", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Cardiologist • 15 Years Experience")

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Consultation Fee: ₹1,200 – ₹2,500")
                Text("Avg Wait Time: 30–45 minutes")
                Text("Success Rate: 92%")
            }
        }

        Text("Common Conditions Treated", fontWeight = FontWeight.Bold)

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("• Heart Disease")
                Text("• High Blood Pressure")
                Text("• Chest Pain")
                Text("• Heart Attack Prevention")
            }
        }
    }
}

/* ===================================================
   REVIEWS TAB
=================================================== */

@Composable
fun CardiologistReviewsTab() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "4.8 ★★★★★",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text("Based on 1,247 reviews")

        Divider()

        ReviewCard(
            name = "Michael R.",
            review = "Saved my life! Excellent cardiac care and follow-up."
        )

        ReviewCard(
            name = "Lisa K.",
            review = "Very knowledgeable and explains conditions clearly."
        )

        Button(
            onClick = {},
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("View All Reviews")
        }
    }
}

@Composable
fun ReviewCard(name: String, review: String) {
    Card {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(name, fontWeight = FontWeight.Bold)
            Text(review, fontSize = 14.sp)
        }
    }
}

/* ===================================================
   INFO TAB
=================================================== */

@Composable
fun CardiologistInfoTab() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text("Operating Hours", fontWeight = FontWeight.Bold)

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Mon–Fri: 8AM – 5PM")
                Text("Sat: 9AM – 1PM")
                Text("Sun: Emergency Only")
            }
        }

        Text("Insurance Accepted", fontWeight = FontWeight.Bold)

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("• Blue Cross")
                Text("• Cigna")
                Text("• Humana")
                Text("• Medicare")
            }
        }

        Text("Facilities & Services", fontWeight = FontWeight.Bold)

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("• ECG")
                Text("• Echo")
                Text("• Stress Test")
                Text("• Cardiac Monitor")
            }
        }

        Text("Good to Know", fontWeight = FontWeight.Bold)

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("• Free parking available")
                Text("• Wheelchair accessible")
                Text("• Video consultations available")
                Text("• Same-day appointments possible")
                Text("• 24/7 emergency support")
            }
        }
    }
}