package com.example.collegeapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CoursesScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Filled.MenuBook, contentDescription = "Courses", tint = MaterialTheme.colors.primary, modifier = Modifier.size(50.dp))
        Spacer(Modifier.height(12.dp))
        Text("Browse all our offered courses.", fontSize = 18.sp, fontWeight = FontWeight.Medium)
    }
}
