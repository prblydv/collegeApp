package com.example.collegeapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.collegeapp.model.StudentProfile

@Composable
fun StudentProfileView(profile: StudentProfile) {
    Card(Modifier.padding(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text("Name: ${profile.name}")
            Text("Father's Name: ${profile.fatherName}")
            Text("Mother's Name: ${profile.motherName}")
            Text("Address: ${profile.address}")
            Text("Pincode: ${profile.pincode}")
            Text("Aadhaar: ${profile.aadhaar}")
            Text("DOB: ${profile.dob}")
            Text("Mobile: ${profile.mobile}")
            Text("Email: ${profile.email}")
            Text("Status: ${profile.status}")

        }
    }
}
