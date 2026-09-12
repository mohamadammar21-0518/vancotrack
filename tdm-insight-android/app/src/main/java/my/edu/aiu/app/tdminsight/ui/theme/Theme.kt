package my.edu.aiu.app.tdminsight.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

// ── Colour tokens ───────────────────────────────────────────────────────────
object TdmPalette {
    val Navy900   = Color(0xFF0A1E35)
    val Navy800   = Color(0xFF0F2A4A)
    val Navy700   = Color(0xFF163860)
    val Teal600   = Color(0xFF0C9B8A)
    val Teal400   = Color(0xFF1DBFAB)
    val Teal100   = Color(0xFFD0F5F1)
    val Ink       = Color(0xFF12233F)
    val Muted     = Color(0xFF50627C)
    val Border    = Color(0xFFDCE5F0)
    val PageBg    = Color(0xFFF4F7FB)
    val White     = Color(0xFFFFFFFF)
    val Error     = Color(0xFFB42318)
    val ErrorBg   = Color(0xFFFEF3F2)
    val Warning   = Color(0xFF7A4F00)
    val WarningBg = Color(0xFFFFF8E6)
    val Success   = Color(0xFF0D6E3A)
    val SuccessBg = Color(0xFFEAF6EE)
    val InfoBlue  = Color(0xFF004B87)
    val InfoBg    = Color(0xFFEDF4FF)
}

private val TdmColorScheme = lightColorScheme(
    primary          = TdmPalette.Teal600,
    onPrimary        = TdmPalette.White,
    primaryContainer = TdmPalette.Teal100,
    onPrimaryContainer = TdmPalette.Navy800,
    secondary        = TdmPalette.Navy800,
    onSecondary      = TdmPalette.White,
    secondaryContainer = TdmPalette.Navy700,
    onSecondaryContainer = TdmPalette.White,
    background       = TdmPalette.PageBg,
    onBackground     = TdmPalette.Ink,
    surface          = TdmPalette.White,
    onSurface        = TdmPalette.Ink,
    surfaceVariant   = Color(0xFFEDF1F7),
    onSurfaceVariant = TdmPalette.Muted,
    outline          = TdmPalette.Border,
    error            = TdmPalette.Error,
    onError          = TdmPalette.White,
)

// ── Typography ───────────────────────────────────────────────────────────────
private val TdmTypography = Typography(
    displaySmall = TextStyle(
        fontWeight = FontWeight.Black,
        fontSize   = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize   = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.3).sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize   = 24.sp,
        lineHeight = 30.sp,
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize   = 20.sp,
        lineHeight = 26.sp,
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = 18.sp,
        lineHeight = 24.sp,
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 12.sp,
        lineHeight = 18.sp,
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize   = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize   = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    ),
)

// ── Shape system ─────────────────────────────────────────────────────────────
private val TdmShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small      = RoundedCornerShape(8.dp),
    medium     = RoundedCornerShape(12.dp),
    large      = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

// ── Theme entry point ────────────────────────────────────────────────────────
@Composable
fun TdmInsightTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TdmColorScheme,
        typography  = TdmTypography,
        shapes      = TdmShapes,
        content     = content,
    )
}
