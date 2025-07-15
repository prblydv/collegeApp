package com.example.collegeapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RoleSelectionScreen(onRoleSelected: (String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Who are you?", style = MaterialTheme.typography.h6)
        Spacer(Modifier.height(24.dp))
        Button(onClick = { onRoleSelected("student") }, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text("Student")
        }
        Button(onClick = { onRoleSelected("teacher") }, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text("Teacher")
        }
        Button(onClick = { onRoleSelected("admin") }, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text("Admin")
        }
    }
}
