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
   MAIN SCREEN
=================================================== */

@Composable
fun GynecologistScreen(
    onBack: () -> Unit = {},
    onBookNow: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Reviews", "Info")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gynecologist") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
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
    ) { padding ->

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {

            /* ---------------- TABS ---------------- */
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

            Spacer(modifier = Modifier.height(12.dp))

            /* ---------------- CONTENT ---------------- */
            when (selectedTab) {
                0 -> GynecologistOverview()
                1 -> GynecologistReviews()
                2 -> GynecologistInfo()
            }

            Spacer(modifier = Modifier.height(80.dp)) // space for bottom button
        }
    }
}

/* ===================================================
   OVERVIEW TAB
=================================================== */

@Composable
fun GynecologistOverview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Dr. Priya Sharma",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text("Gynecologist • 13 Years Experience")

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Consultation Fee: ₹800 – ₹1,600")
                Text("Avg Wait Time: 25–35 minutes")
                Text("Success Rate: 93%")
            }
        }

        Text("Common Conditions Treated", fontWeight = FontWeight.Bold)

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("• Pregnancy Care")
                Text("• Menstrual Issues")
                Text("• PCOS")
                Text("• Annual Screenings")
            }
        }
    }
}

/* ===================================================
   REVIEWS TAB
=================================================== */

@Composable
fun GynecologistReviews() {
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

        GynecologistReviewItem(
            name = "Amanda B.",
            review = "Compassionate care throughout my pregnancy journey."
        )

        GynecologistReviewItem(
            name = "Rachel G.",
            review = "Very professional and makes you feel comfortable."
        )

        Button(
            onClick = {},
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("View All Reviews")
        }
    }
}

/* ===================================================
   REVIEW ITEM
=================================================== */

@Composable
fun GynecologistReviewItem(
    name: String,
    review: String
) {
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
fun GynecologistInfo() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text("Operating Hours", fontWeight = FontWeight.Bold)

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Mon–Fri: 8AM – 6PM")
                Text("Sat: 9AM – 2PM")
                Text("Sun: Closed")
            }
        }

        Text("Insurance Accepted", fontWeight = FontWeight.Bold)

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("• Blue Cross")
                Text("• Aetna")
                Text("• UnitedHealth")
                Text("• Medicaid")
            }
        }

        Text("Facilities & Services", fontWeight = FontWeight.Bold)

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("• Ultrasound")
                Text("• Lab Tests")
                Text("• Mammography")
                Text("• Colposcopy")
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
