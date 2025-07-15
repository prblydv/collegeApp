package com.example.collegeapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NewsScreen() {
    // Simulate news/notifications
    val notifications = listOf(
        "Admission Open for 2024–25 batch!",
        "Last date for fee payment: 30th July.",
        "Annual Sports Meet announced for August.",
        "Welcome to all new students!"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = MaterialTheme.colors.primary, modifier = Modifier.size(50.dp))
        Spacer(Modifier.height(12.dp))
        Text("Latest News & Notifications", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(18.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(notifications.size) { index ->
                Card(
                    elevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        notifications[index],
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
