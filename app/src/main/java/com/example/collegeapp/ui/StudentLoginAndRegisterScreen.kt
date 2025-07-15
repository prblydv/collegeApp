package com.example.collegeapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    when {
        showChoice -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = { isRegistering = true; showChoice = false },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) { Text("I am a New Student") }
                Button(
                    onClick = { isLoggingIn = true; showChoice = false },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) { Text("I already have a College Login") }
            }
        }
        isRegistering -> {
            StudentRegistrationForm { form, password ->
                coroutineScope.launch {
                    errorMsg = null
                    val err = repo.registerStudent(form, password)
                    if (err == null) {
                        approvalPending = true
                    } else {
                        errorMsg = err
                    }
                }
            }
            if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colors.error)
            if (approvalPending) ApprovalPendingScreen()
        }
        isLoggingIn -> {
            StudentLoginForm { email, password ->
                coroutineScope.launch {
                    errorMsg = null
                    val (err, prof) = repo.loginStudent(email, password)
                    if (err == null && prof != null) {
                        if (prof.status == "approved") {
                            profile = prof
                        } else {
                            approvalPending = true
                        }
                    } else {
                        errorMsg = err
                    }
                }
            }
            if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colors.error)
            if (profile != null) StudentProfileView(profile!!)
            if (approvalPending) ApprovalPendingScreen()
        }
    }
}
