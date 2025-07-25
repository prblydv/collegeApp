package com.example.collegeapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RoleSelectionScreen(onRoleSelected: (String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (MaterialTheme.colors.isLight) {
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFF2F3FF), Color.White)
                    )
                } else {
                    SolidColor(MaterialTheme.colors.background) // full black or dark in dark mode!
                }
            )
    ) {
        // INFO BANNER
        Card(
            shape = RoundedCornerShape(18.dp),
            elevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Column(
                Modifier
                    .background(MaterialTheme.colors.surface)
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Info",
                        tint = MaterialTheme.colors.primary, // theme-aware
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Important!",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.primary,
                        fontSize = 17.sp
                    )
                }
                Spacer(Modifier.height(7.dp))
                Text(
                    text = "This app's login is strictly for current Yogiraj College students. If you are not already enrolled, please do not attempt to register.",
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 5.dp),
                    lineHeight = 20.sp
                )
            }
        }
        Spacer(Modifier.height(40.dp))

        // Headline
        Text(
            "Who are you?",
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colors.onBackground,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 18.dp)
        )

        // BUTTONS
        val buttonColors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary
        )
        Button(
            onClick = { onRoleSelected("student") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 38.dp, vertical = 5.dp)
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = buttonColors,
            elevation = ButtonDefaults.elevation(8.dp)
        ) {
            Text("Student", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

