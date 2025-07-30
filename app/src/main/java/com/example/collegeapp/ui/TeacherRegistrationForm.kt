package com.example.collegeapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.collegeapp.model.TeacherProfile

@Composable
fun TeacherRegistrationForm(onRegister: (TeacherProfile, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var teacherId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isValid = name.isNotBlank() && email.isNotBlank() && subject.isNotBlank() && teacherId.isNotBlank() && password.length >= 6

    Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") })
        OutlinedTextField(value = teacherId, onValueChange = { teacherId = it }, label = { Text("Teacher ID") })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password (min 6 chars)") }, visualTransformation = PasswordVisualTransformation())
        Button(
            onClick = {
                onRegister(
                    TeacherProfile(
                        name = name,
                        email = email,
                        subject = subject,
                        teacherId = teacherId,
                        status = "pending"
                    ),
                    password
                )
            },
            enabled = isValid,
            modifier = Modifier.padding(top = 16.dp)
        ) { Text("Register") }
    }
}
