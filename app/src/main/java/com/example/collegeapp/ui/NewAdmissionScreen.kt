package com.example.collegeapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NewAdmissionScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Filled.Language, contentDescription = "Web Registration", tint = MaterialTheme.colors.primary, modifier = Modifier.size(50.dp))
        Spacer(Modifier.height(12.dp))
        Text("Register online for classes, exams, and more.", fontSize = 18.sp, fontWeight = FontWeight.Medium)
    }
}
