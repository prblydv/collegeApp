package com.example.collegeapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.collegeapp.model.AdminProfile

@Composable
fun AdminLoginAndRegisterScreen() {
    var isSignedIn by remember { mutableStateOf(false) }
    var profile by remember { mutableStateOf<AdminProfile?>(null) }

    if (!isSignedIn) {
        Button(
            onClick = { isSignedIn = true },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) { Text("Sign in with Google (Admin)") }
    } else {
        // Admin is always approved (or implement approval logic)
        profile = AdminProfile("Admin Name", "admin@school.com")
        AdminDashboard(profile)
    }
}
