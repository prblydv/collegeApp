package com.example.collegeapp.ui

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import com.example.collegeapp.model.AdminProfile

@Composable
fun AdminDashboard(profile: AdminProfile?) {
    Text("Welcome, ${profile?.name ?: "Admin"}! (Admin Dashboard)")
}
