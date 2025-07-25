import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.collegeapp.model.EtihadAltisLight

val AppTypography = Typography(
    h1 = TextStyle(
        fontFamily = EtihadAltisLight,
        fontWeight = FontWeight.Normal,
        fontSize = 30.sp
    ),
    body1 = TextStyle(
        fontFamily = EtihadAltisLight,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
    // Define other text styles as needed
)
