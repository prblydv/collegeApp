package com.example.collegeapp.ui

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import com.example.collegeapp.model.StudentProfile

@Composable
fun StudentDashboard(profile: StudentProfile?) {
    Text("Welcome, ${profile?.name ?: "Student"}! (Student Dashboard)")
}
