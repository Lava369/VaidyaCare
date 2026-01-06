package com.example.vaidyacare.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun JointScreen(
    onBackToWelcome: () -> Unit = {},
    onNextToRoleSelection: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Joint Screen", fontSize = 26.sp)

        Spacer(modifier = Modifier.height(40.dp))

        // 🔙 Go Back to Welcome
        Button(onClick = { onBackToWelcome() }) {
            Text("Back to Welcome Screen")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 👉 Go Forward to Role Selection
        Button(onClick = { onNextToRoleSelection() }) {
            Text("Go to Role Selection")
        }
    }
}
