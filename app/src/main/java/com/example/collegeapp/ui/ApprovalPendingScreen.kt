package com.example.collegeapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.collegeapp.data.UserRepository

@Composable
fun ApprovalPendingScreen(email: String, repo: UserRepository, onApproved: () -> Unit) {
    var status by remember { mutableStateOf("pending") }
    DisposableEffect(email) {
        val listener = repo.listenForApproval(email) { newStatus ->
            status = newStatus
            if (newStatus == "approved") onApproved()
        }
        onDispose { listener }
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(elevation = 8.dp, modifier = Modifier.padding(24.dp)) {
            Column(Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                if (status == "pending") {
                    Text("Your registration is pending admin approval. Please wait.", style = MaterialTheme.typography.h6)
                    Spacer(Modifier.height(16.dp))
                    CircularProgressIndicator()
                } else if (status == "approved") {
                    Text("Approved! Please continue to login.", color = MaterialTheme.colors.primary)
                } else {
                    Text("Unknown status: $status", color = MaterialTheme.colors.error)
                }
            }
        }
    }
}
