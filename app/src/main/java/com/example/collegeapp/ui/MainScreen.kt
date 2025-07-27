package com.example.collegeapp.ui

import android.util.Log
import androidx.compose.animation.core.RepeatMode
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material.icons.filled.Brightness3
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.text.font.FontWeight
import com.example.collegeapp.model.TabItem
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.times
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import com.example.collegeapp.R

val barHeight = 70.dp  // Instagram-like is typically 64–72dp
val pillCornerRadius = 28.dp // For a more pill-like appearance

@Composable
fun SlidingPillNavBarWithIcons(
    tabs: List<TabItem>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    pillColor: Color = Color.Black,
    backgroundColor: Color = Color.White,
    contentColor: Color = Color.Black
) {
    val itemWidth: Dp = 74.dp
    val animatedOffset by animateDpAsState(
        targetValue = selectedIndex * itemWidth,
        animationSpec = tween(durationMillis = 320)
    )
    val insets = WindowInsets.navigationBars.asPaddingValues() // for gesture bar

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight)
            .padding(bottom = insets.calculateBottomPadding().coerceAtLeast(10.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(backgroundColor)
    ) {
        // Sliding pill
        Box(
            Modifier
                .offset(x = animatedOffset)
                .size(width = itemWidth, height = barHeight)
                .clip(RoundedCornerShape(22.dp))
                .background(pillColor)
        )
        // Row of tab icons
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                Box(
                    Modifier
                        .width(itemWidth)
                        .fillMaxHeight()
                        .clickable { onTabSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = if (selectedIndex == index)
                            MaterialTheme.colors.onPrimary
                        else
                            MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val tabs = listOf(
        TabItem("News", Icons.Filled.Notifications),
        TabItem("Online Payment", Icons.Filled.Payment),
        TabItem("Courses", Icons.AutoMirrored.Filled.MenuBook),
        TabItem("New Admission", Icons.Filled.Language),
        TabItem("Login", Icons.Filled.Person)
    )
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showInfoDialog by remember { mutableStateOf(false) }
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("College Information") },
            text = {
                Text(
                    "Yogiraj Shri Krishna Mahavidyalaya \n (Affiliated by DBRAU, Agra)\n\n"+
                            "Address:\nGT Road Bypass Bhongaon Dist: Mainpuri, UP, India 205262\n\n" +
                            "Contact:\n8384843193 \n8126318967"
                )
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            // College header
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
                    .padding(top = 24.dp, bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.college_logo3),
                        contentDescription = "College logo",
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)      // optional: makes it round
                    )
                    Spacer(Modifier.width(8.dp))
                    AnimatedNameSplash(
                        fullName = "YOGIRAJ",
                        textColor = Color.White,
                        fontSize = 32,
                        modifier = Modifier
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Info",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Filled.WbSunny else Icons.Filled.Brightness3,
                            contentDescription = "Toggle Theme",
                            tint = Color.White
                        )
                    }

                }
            }
        },
        bottomBar = {
            SlidingPillNavBarWithIcons(
                tabs = tabs,
                selectedIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it },
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .fillMaxWidth(),
                pillColor = MaterialTheme.colors.primary,
                backgroundColor = if (MaterialTheme.colors.isLight) Color.White else Color(0xFF23242A),
                contentColor = if (MaterialTheme.colors.isLight) Color.Black else Color.White
            )
        }

    ) { paddingValues ->
        // Content area, stretched edge-to-edge
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTabIndex) {
                0 -> NewsScreen()
                1 -> OnlinePaymentScreen()
                2 -> CoursesScreen()
                3 -> NewAdmissionScreen()
                4 -> LoginTabScreen()
            }
        }
    }
}
