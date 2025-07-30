package com.example.collegeapp.ui
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.collegeapp.R

val TitleFontFamily = FontFamily(
    Font(R.font.title_font)
)
@Composable
fun AnimatedNameSplash(
    modifier: Modifier = Modifier,
    fullName: String = "YOGIRAJ SKMV",
    textColor: Color = Color(0xFF6658D3),
    animationDurationPerLetter: Int = 25,
    fontSize: Int = 12
) {
    var lettersVisible by remember { mutableStateOf(0) }

    // Animate letters appearing one by one
    LaunchedEffect(fullName) {
        for (i in 1..fullName.length) {
            lettersVisible = i
            kotlinx.coroutines.delay(animationDurationPerLetter.toLong())
        }
    }

    Row(modifier = modifier) {
        for (i in 0 until lettersVisible.coerceAtMost(fullName.length)) {
            // Per letter animated scale and alpha on appear
            val scale = remember { Animatable(0.7f) }
            val alpha = remember { Animatable(0f) }

            LaunchedEffect(Unit) {
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                )
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 400)
                )
            }

            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .scale(scale.value)
                    .alpha(alpha.value)
            ) {
                Text(
                    text = fullName[i].toString(),
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = TitleFontFamily,   // ← use your custom font

                    color = textColor
                )
            }
        }
    }
}
