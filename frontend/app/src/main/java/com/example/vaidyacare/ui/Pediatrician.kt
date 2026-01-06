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
fun PediatricianScreen(
    onBack: () -> Unit = {},
    onBookNow: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Reviews", "Info")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pediatrician") },
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
                0 -> PediatricianOverview()
                1 -> PediatricianReviews()
                2 -> PediatricianInfo()
            }

            Spacer(modifier = Modifier.height(80.dp)) // space for bottom button
        }
    }
}

/* ===================================================
   OVERVIEW TAB
=================================================== */

@Composable
fun PediatricianOverview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Dr. Priya Sharma",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text("Pediatrician • 14 Years Experience")

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Consultation Fee: ₹600 – ₹1,200")
                Text("Avg Wait Time: 15–25 minutes")
                Text("Success Rate: 96%")
            }
        }

        Text("Common Conditions Treated", fontWeight = FontWeight.Bold)

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("• Vaccinations")
                Text("• Growth Monitoring")
                Text("• Childhood Illnesses")
                Text("• Development Checks")
            }
        }
    }
}

/* ===================================================
   REVIEWS TAB
=================================================== */

@Composable
fun PediatricianReviews() {
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

        PediatricianReviewItem(
            name = "Jennifer P.",
            review = "So gentle with kids! My daughter loves visiting."
        )

        PediatricianReviewItem(
            name = "Robert H.",
            review = "Excellent pediatrician, very patient and kind."
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
fun PediatricianReviewItem(
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
fun PediatricianInfo() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text("Operating Hours", fontWeight = FontWeight.Bold)

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Mon–Fri: 8AM – 6PM")
                Text("Sat: 9AM – 3PM")
                Text("Sun: Closed")
            }
        }

        Text("Insurance Accepted", fontWeight = FontWeight.Bold)

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("• Blue Cross")
                Text("• Aetna")
                Text("• Medicaid")
                Text("• CHIP")
            }
        }

        Text("Facilities & Services", fontWeight = FontWeight.Bold)

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("• Vaccination Center")
                Text("• Nebulizer")
                Text("• Growth Charts")
                Text("• Lab")
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
