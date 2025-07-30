package com.example.collegeapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun StudentLoginForm(onLogin: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isValid = email.isNotBlank() && password.length >= 6

    Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email/Login ID") })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())
        Button(
            onClick = { onLogin(email, password) },
            enabled = isValid,
            modifier = Modifier.padding(top = 16.dp)
        ) { Text("Login") }
    }
}
