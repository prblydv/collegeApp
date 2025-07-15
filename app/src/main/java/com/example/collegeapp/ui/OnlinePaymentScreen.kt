package com.example.collegeapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payment
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnlinePaymentScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Filled.Payment, contentDescription = "Payment", tint = MaterialTheme.colors.primary, modifier = Modifier.size(50.dp))
        Spacer(Modifier.height(12.dp))
        Text("Pay your fees online easily and securely.", fontSize = 18.sp, fontWeight = FontWeight.Medium)
    }
}
