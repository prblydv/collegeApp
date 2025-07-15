package com.example.collegeapp.ui

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import com.example.collegeapp.model.TeacherProfile

@Composable
fun TeacherDashboard(profile: TeacherProfile?) {
    Text("Welcome, ${profile?.name ?: "Teacher"}! (Teacher Dashboard)")
}
