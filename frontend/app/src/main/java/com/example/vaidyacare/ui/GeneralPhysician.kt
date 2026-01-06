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
   NAVIGATION WRAPPER
=================================================== */

@Composable
fun GeneralPhysicianScreen(
    onBack: () -> Unit = {},
    onBookNow: () -> Unit = {}
) {
    BookAppointmentScreen(
        onBack = onBack,
        onBookNow = onBookNow
    )
}

/* ===================================================
   MAIN SCREEN (SCROLL ENABLED)
=================================================== */

@Composable
fun BookAppointmentScreen(
    onBack: () -> Unit = {},
    onBookNow: () -> Unit = {}
) {

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Reviews", "Info")
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("General Physician") },
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

            /* ---------------- Tabs (FIXED) ---------------- */
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
                    0 -> OverviewTab()
                    1 -> ReviewsTab()
                    2 -> InfoTab()
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
fun OverviewTab() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text("Dr. Priya Sharma", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("General Physician • 12 Years Experience")

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Consultation Fee: ₹400 - ₹800")
                Text("Avg Wait Time: 15–30 minutes")
                Text("Success Rate: 95%")
            }
        }

        Text("Common Conditions Treated", fontWeight = FontWeight.Bold)

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("• Fever & Infections")
                Text("• Diabetes Management")
                Text("• Hypertension")
                Text("• Annual Check-ups")
            }
        }
    }
}

/* ===================================================
   REVIEWS TAB
=================================================== */

@Composable
fun ReviewsTab() {
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

        ReviewItem(
            name = "Sarah M.",
            review = "Very thorough and caring. Explained everything clearly."
        )

        ReviewItem(
            name = "John D.",
            review = "Quick appointment and professional service."
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
fun ReviewItem(name: String, review: String) {
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
fun InfoTab() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text("Operating Hours", fontWeight = FontWeight.Bold)

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Mon–Fri: 9AM – 6PM")
                Text("Sat: 10AM – 2PM")
                Text("Sun: Closed")
            }
        }

        Text("Insurance Accepted", fontWeight = FontWeight.Bold)

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("• Blue Cross")
                Text("• Aetna")
                Text("• UnitedHealth")
                Text("• Medicare")
            }
        }

        Text("Facilities & Services", fontWeight = FontWeight.Bold)

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("• Lab Tests")
                Text("• X-Ray")
                Text("• ECG")
                Text("• Pharmacy")
            }
        }
    }
}
