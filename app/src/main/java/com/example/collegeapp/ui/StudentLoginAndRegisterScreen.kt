package com.example.collegeapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.collegeapp.data.UserRepository
import com.example.collegeapp.model.StudentProfile
import kotlinx.coroutines.launch
@Composable
fun StudentLoginAndRegisterScreen() {
    val repo = remember { UserRepository() }
    var showChoice by remember { mutableStateOf(true) }
    var isRegistering by remember { mutableStateOf(false) }
    var isLoggingIn by remember { mutableStateOf(false) }
    var profile by remember { mutableStateOf<StudentProfile?>(null) }
    var approvalPending by remember { mutableStateOf(false) }
    var pendingEmail by remember { mutableStateOf<String?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (showChoice) {
            Button(onClick = { isRegistering = true; showChoice = false }) { Text("Register") }
            Spacer(Modifier.height(16.dp))
            Button(onClick = { isLoggingIn = true; showChoice = false }) { Text("Login") }
        }

        if (isRegistering) {
            StudentRegistrationForm { form, password ->
                coroutineScope.launch {
                    errorMsg = null
                    val err = repo.registerStudentPending(form, password)
                    if (err == null) {
                        approvalPending = true
                        pendingEmail = form.email
                        isRegistering = false
                    } else {
                        errorMsg = err
                    }
                }
            }
        }

        if (approvalPending && pendingEmail != null) {
            WaitingForApprovalScreen(pendingEmail!!)
        }

        if (isLoggingIn) {
            StudentLoginForm { email, password ->
                coroutineScope.launch {
                    errorMsg = null
                    val (err, userProfile) = repo.loginStudent(email, password)
                    if (err == null && userProfile != null) {
                        profile = userProfile
                        isLoggingIn = false
                    } else {
                        errorMsg = err
                    }
                }
            }
        }

        profile?.let { ShowStudentProfile(it) }

        errorMsg?.let {
            Text(text = it, color = Color.Red)
        }
    }
}

@Composable
fun ShowStudentProfile(profile: StudentProfile) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome, ${profile.name}")
        Spacer(Modifier.height(8.dp))
        Text("Email: ${profile.email}")
        Spacer(Modifier.height(8.dp))
        Text("Mobile: ${profile.mobile}")
        Spacer(Modifier.height(8.dp))
        // Add other fields as you like
        // Example: Text("Aadhaar: ${profile.aadhaar}")
    }
}
