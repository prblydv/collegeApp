package com.example.collegeapp.ui.theme

import AppTypography
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color



private val DarkColorPalette = darkColors(
    primary = Color(0xFF7C0A39),        // Teal as primary
    primaryVariant = Color(0xFF393E46), // Grey variant
    secondary = Color(0xFF7C0A39),      // Teal secondary as well (or choose another)
    background = Color(0xFF222831),     // Black background
    surface = Color(0xFF393E46),        // Grey surface
    onPrimary = Color(0xFFEEEEEE),      // Space (light) text on teal
    onSecondary = Color(0xFFEEEEEE),    // Space (light) text on secondary
    onBackground = Color(0xFFEEEEEE),   // Space (light) text on dark background
    onSurface = Color(0xFFEEEEEE)       // Space (light) text on surface
)

private val LightColorPalette = lightColors(
    primary = Color(0xFF7C0A39),         // Teal as primary
    primaryVariant = Color(0xFF393E46),  // Grey variant
    secondary = Color(0xFF7C0A39),       // Teal secondary
    background = Color(0xFFEEEEEE),       // Space (light) background
    surface = Color(0xFFEEEEEE),          // Space (light) surface
    onPrimary = Color(0xFF222831),        // Black text on teal
    onSecondary = Color(0xFF222831),      // Black text on secondary
    onBackground = Color(0xFF222831),     // Black text on light background
    onSurface = Color(0xFF222831)         // Black text on surface
)

@Composable
fun CollegeAppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColorPalette else LightColorPalette,
        typography = AppTypography, // <-- use the correct value!
        shapes = AppShapes,         // <-- use the correct value!
        content = content
    )
}
