@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vaidyacare.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vaidyacare.R

/* ---------------- EARNINGS & PAYMENTS SCREEN ---------------- */

@Composable
fun EarningPaymentsScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Earnings & Payments",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Track your income",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            /* -------- TOTAL EARNINGS CARD -------- */

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF18C5A7),
                                    Color(0xFF0FAF9A)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {

                        Text(
                            text = "Total Earnings (This Month)",
                            color = Color.White,
                            fontSize = 14.sp
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "₹45,500",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(
                                    id = R.drawable.ic_trending_up
                                ),
                                contentDescription = "Growth",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(Modifier.width(6.dp))

                            Text(
                                text = "+12% from last month",
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            /* -------- SMALL EARNINGS CARDS -------- */

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                EarningsSmallCard(
                    title = "Today",
                    amount = "₹2,400",
                    modifier = Modifier.weight(1f)
                )

                EarningsSmallCard(
                    title = "This Week",
                    amount = "₹12,800",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/* ---------------- SMALL CARD ---------------- */

@Composable
fun EarningsSmallCard(
    title: String,
    amount: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = amount,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
