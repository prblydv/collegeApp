import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun AnimatedNameSplash(
    modifier: Modifier = Modifier,
    fullName: String = "YOGIRAJ SKMV",
    textColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color(0xFF6658D3),
    animationDurationPerLetter: Int = 250,
    fontSize: Int = 48
) {
    var currentIndex by remember { mutableStateOf(0) }

    // Animate index increment every animationDurationPerLetter milliseconds
    LaunchedEffect(Unit) {
        while (currentIndex < fullName.length) {
            kotlinx.coroutines.delay(animationDurationPerLetter.toLong())
            currentIndex++
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Display substring up to currentIndex for the typing effect
        Text(
            text = fullName.take(currentIndex),
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.wrapContentWidth()
        )
    }
}
