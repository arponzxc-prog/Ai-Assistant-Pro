package com.arponzxc.novaai.devstudio.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object StudioPalette {
    val BgCanvas = Color(0xFF060A14)
    val BgSurface = Color(0xFF0F172A)
    val BgCard = Color(0xFF162033)
    val BgElevated = Color(0xFF1E293B)
    val BorderSubtle = Color(0xFF28354D)

    val CyanAccent = Color(0xFF38BDF8)
    val IndigoAccent = Color(0xFF818CF8)
    val EmeraldSuccess = Color(0xFF10B981)
    val AmberWarn = Color(0xFFF59E0B)
    val RoseError = Color(0xFFF43F5E)

    val TextPrimary = Color(0xFFF8FAFC)
    val TextSecondary = Color(0xFFCBD5E1)
    val TextMuted = Color(0xFF94A3B8)

    val CodeBg = Color(0xFF090E1A)
    val CodeKeyword = Color(0xFFC084FC)
    val CodeString = Color(0xFF34D399)
    val CodeType = Color(0xFF38BDF8)
    val CodeComment = Color(0xFF64748B)
}

private val StudioDarkColors = darkColorScheme(
    primary = StudioPalette.CyanAccent,
    onPrimary = Color(0xFF031524),
    secondary = StudioPalette.IndigoAccent,
    onSecondary = Color(0xFF090D24),
    tertiary = StudioPalette.EmeraldSuccess,
    background = StudioPalette.BgCanvas,
    onBackground = StudioPalette.TextPrimary,
    surface = StudioPalette.BgSurface,
    onSurface = StudioPalette.TextPrimary,
    surfaceVariant = StudioPalette.BgCard,
    onSurfaceVariant = StudioPalette.TextSecondary,
    outline = StudioPalette.BorderSubtle,
    error = StudioPalette.RoseError,
    onError = Color.White
)

private val StudioTypography = Typography(
    headlineSmall = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.3).sp
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = FontFamily.SansSerif
    )
)

@Composable
fun DevStudioTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = StudioDarkColors,
        typography = StudioTypography,
        content = content
    )
}
