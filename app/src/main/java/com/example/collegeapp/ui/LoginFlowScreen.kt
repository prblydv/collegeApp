package com.example.collegeapp.ui

import androidx.compose.runtime.*

@Composable
fun LoginFlowScreen() {
    var role by remember { mutableStateOf<String?>(null) }

    if (role == null) {
        RoleSelectionScreen { selectedRole ->
            role = selectedRole
        }
    } else {
        when (role) {
            "student" -> StudentLoginAndRegisterScreen()
            "teacher" -> TeacherLoginAndRegisterScreen()
            "admin" -> AdminLoginAndRegisterScreen()
        }
    }
}
