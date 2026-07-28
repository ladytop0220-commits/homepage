package kr.annyeong.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// === 안녕 브랜드 색상 : 편안한 초록 + 높은 대비 ===
val AnnyeongGreen = Color(0xFF2E7D5B)
val AnnyeongGreenDark = Color(0xFF1B5E42)
val AnnyeongCall = Color(0xFF1B8A4B)   // 전화 버튼 (초록)
val AnnyeongMessage = Color(0xFF1565C0) // 문자 버튼 (파랑)
val AnnyeongWarn = Color(0xFFB00020)    // 긴급/주의 (빨강)
val AnnyeongCream = Color(0xFFF6F3EC)

private val LightColors = lightColorScheme(
    primary = AnnyeongGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB6E3CC),
    onPrimaryContainer = Color(0xFF06251A),
    secondary = AnnyeongMessage,
    onSecondary = Color.White,
    background = AnnyeongCream,
    onBackground = Color(0xFF1A1A1A),
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    error = AnnyeongWarn,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF6FD8A6),
    onPrimary = Color(0xFF06251A),
    background = Color(0xFF10140F),
    onBackground = Color(0xFFF2F2F2),
    surface = Color(0xFF1B211A),
    onSurface = Color(0xFFF2F2F2),
    error = Color(0xFFFF6E6E),
)

// 어르신 가독성을 위해 기본 글자 크기를 크게 재정의
private val BigTypography = Typography(
    displayLarge = TextStyle(fontSize = 44.sp, fontWeight = FontWeight.Bold),
    headlineLarge = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 22.sp, lineHeight = 30.sp),
    bodyMedium = TextStyle(fontSize = 20.sp, lineHeight = 28.sp),
    labelLarge = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold),
)

@Composable
fun AnnyeongTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = BigTypography,
        content = content,
    )
}
