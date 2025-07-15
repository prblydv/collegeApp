package com.example.collegeapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.collegeapp.data.UserRepository
import com.example.collegeapp.model.TeacherProfile
import kotlinx.coroutines.launch

@Composable
fun TeacherLoginAndRegisterScreen() {
    val context = LocalContext.current
    val repo = remember { UserRepository() }        // ✅ Correct
    var isSignedIn by remember { mutableStateOf(false) }
    var registrationStatus by remember { mutableStateOf<String?>(null) }
    var profile by remember { mutableStateOf<TeacherProfile?>(null) }
    val coroutineScope = rememberCoroutineScope()

    if (!isSignedIn) {
        Button(
            onClick = {
                isSignedIn = true
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) { Text("Sign in with Google (Teacher)") }
    } else {
        LaunchedEffect(isSignedIn) {
            val (status, profileObj) = repo.getTeacherApprovalStatus()
            registrationStatus = status
            profile = profileObj
        }
        when (registrationStatus) {
            null -> CircularProgressIndicator()
            "approved" -> TeacherDashboard(profile)
            "pending" -> ApprovalPendingScreen()
            "not_registered" -> TeacherRegistrationForm(onRegister = { formData, password ->
                coroutineScope.launch {
                    repo.registerTeacher(formData, password)
                    registrationStatus = "pending"
                }
            })

        }
    }
}

@Composable
fun TeacherRegistrationForm(onRegister: (TeacherProfile) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var teacherId by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    val isValid = name.isNotBlank() && email.isNotBlank() && teacherId.isNotBlank() && subject.isNotBlank()

    Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = teacherId, onValueChange = { teacherId = it }, label = { Text("Teacher ID") })
        OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") })
        Button(
            onClick = { onRegister(TeacherProfile(name, email, teacherId, subject, "pending")) },
            enabled = isValid,
            modifier = Modifier.padding(top = 16.dp)
        ) { Text("Submit") }
    }
}
