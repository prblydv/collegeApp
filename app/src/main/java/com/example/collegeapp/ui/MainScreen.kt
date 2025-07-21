package com.example.collegeapp.ui

import android.util.Log
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import com.example.collegeapp.model.TabItem
import com.example.collegeapp.ui.NewsScreen

@Composable
fun MainScreen() {

    val tabs = listOf(
        TabItem("News", Icons.Filled.Notifications),   // <-- Add this line
        TabItem("Online Payment", Icons.Filled.Payment),
        TabItem("Courses", Icons.AutoMirrored.Filled.MenuBook),
        TabItem("New Admission", Icons.Filled.Language),
        TabItem("Login", Icons.Filled.Person)
    )
    var selectedTabIndex by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colors.primary.copy(alpha = 0.10f),
                        Color.White
                    )
                )
            )
    ) {
        Column {
            // HEADER (Logo + College Name)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colors.primary,
                                MaterialTheme.colors.secondary
                            )
                        ),
                        shape = RoundedCornerShape(bottomEnd = 32.dp, bottomStart = 32.dp)
                    )
                    .padding(top = 32.dp, bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // App Icon Circle with Y letter (replace with logo as needed)
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color.White.copy(alpha = 0.25f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Y",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.onPrimary
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "YOGIRAJ COLLEGE",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Tabs (Modern Card look)
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex])
                            .height(4.dp)
                            .padding(horizontal = 28.dp),
                        color = MaterialTheme.colors.primary
                    )
                },
                edgePadding = 8.dp,
                backgroundColor = Color.Transparent
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        icon = {
                            Icon(tab.icon, contentDescription = tab.label)
                        },
                        text = {
                            Text(
                                tab.label,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) MaterialTheme.colors.primary else Color.Gray
                            )
                        },
                        modifier = Modifier
                            .height(60.dp)
                            .padding(vertical = 6.dp)
                    )
                }
            }

            // Main Content (card, padding, elevation)
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp)
                    .shadow(2.dp, RoundedCornerShape(18.dp))
                    .background(Color.White, RoundedCornerShape(18.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                when (selectedTabIndex) {
                    0 -> NewsScreen()            // <-- Add this line
                    1 -> OnlinePaymentScreen()
                    2 -> CoursesScreen()
                    3 -> NewAdmissionScreen()
                    4 -> LoginTabScreen()
                }
            }
        }
    }
}
