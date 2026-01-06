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
fun OrthopedicScreen(
    onBack: () -> Unit = {},
    onBookNow: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Reviews", "Info")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orthopedic") },
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
                0 -> OrthopedicOverview()
                1 -> OrthopedicReviews()
                2 -> OrthopedicInfo()
            }

            Spacer(modifier = Modifier.height(80.dp)) // space for bottom button
        }
    }
}

/* ===================================================
   OVERVIEW TAB
=================================================== */

@Composable
fun OrthopedicOverview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Dr. Priya Sharma",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text("Orthopedic Specialist • 16 Years Experience")

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Consultation Fee: ₹1,200 – ₹2,000")
                Text("Avg Wait Time: 30–40 minutes")
                Text("Success Rate: 91%")
            }
        }

        Text("Common Conditions Treated", fontWeight = FontWeight.Bold)

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("• Fractures")
                Text("• Joint Pain")
                Text("• Sports Injuries")
                Text("• Arthritis")
            }
        }
    }
}

/* ===================================================
   REVIEWS TAB
=================================================== */

@Composable
fun OrthopedicReviews() {
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

        OrthopedicReviewItem(
            name = "Tom W.",
            review = "Fixed my knee injury perfectly. Back to sports!"
        )

        OrthopedicReviewItem(
            name = "Maria S.",
            review = "Great surgeon, successful hip replacement."
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
fun OrthopedicReviewItem(
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
fun OrthopedicInfo() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text("Operating Hours", fontWeight = FontWeight.Bold)

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Mon–Fri: 7AM – 5PM")
                Text("Sat: 8AM – 12PM")
                Text("Sun: Emergency Only")
            }
        }

        Text("Insurance Accepted", fontWeight = FontWeight.Bold)

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("• Blue Cross")
                Text("• Cigna")
                Text("• Workers Comp")
                Text("• Medicare")
            }
        }

        Text("Facilities & Services", fontWeight = FontWeight.Bold)

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("• X-Ray")
                Text("• MRI")
                Text("• Physical Therapy")
                Text("• Surgery Center")
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
