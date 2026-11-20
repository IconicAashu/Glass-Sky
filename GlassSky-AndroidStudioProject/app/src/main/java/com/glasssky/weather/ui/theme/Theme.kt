package com.glasssky.weather.ui.theme

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.glasssky.weather.data.repository.ThemeMode

@Immutable
data class GlassThemeColors(
    val cardBackground: Color,
    val cardBorderTop: Color,
    val cardBorderBottom: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val iconTint: Color,
    val isDark: Boolean
)

val LocalGlassColors = staticCompositionLocalOf {
    GlassThemeColors(
        cardBackground = Color(0x1AFFFFFF),
        cardBorderTop = Color(0x33FFFFFF),
        cardBorderBottom = Color(0x0FFFFFFF),
        textPrimary = Color.White,
        textSecondary = Color.White.copy(alpha = 0.7f),
        textMuted = Color.White.copy(alpha = 0.5f),
        iconTint = Color.White,
        isDark = true
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlue,
    secondary = GoldenSun,
    background = MidnightDark,
    surface = MidnightCard,
    onPrimary = Color.Black,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = SkyDeepBlue,
    secondary = GoldenSun,
    background = Color(0xFFF0F4F8),
    surface = Color(0xDDFFFFFF),
    onPrimary = Color.White,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight
)

@Composable
fun GlassSkyTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemInDark
    }

    val animatedCardBg by animateColorAsState(
        targetValue = if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.72f),
        animationSpec = tween(500),
        label = "GlassCardBg"
    )
    val animatedBorderTop by animateColorAsState(
        targetValue = if (isDark) Color.White.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.90f),
        animationSpec = tween(500),
        label = "GlassBorderTop"
    )
    val animatedBorderBottom by animateColorAsState(
        targetValue = if (isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFCBD5E1).copy(alpha = 0.6f),
        animationSpec = tween(500),
        label = "GlassBorderBottom"
    )
    val animatedTextPrimary by animateColorAsState(
        targetValue = if (isDark) Color.White else Color(0xFF0F172A),
        animationSpec = tween(500),
        label = "GlassTextPrimary"
    )
    val animatedTextSecondary by animateColorAsState(
        targetValue = if (isDark) Color.White.copy(alpha = 0.75f) else Color(0xFF334155),
        animationSpec = tween(500),
        label = "GlassTextSecondary"
    )
    val animatedTextMuted by animateColorAsState(
        targetValue = if (isDark) Color.White.copy(alpha = 0.50f) else Color(0xFF64748B),
        animationSpec = tween(500),
        label = "GlassTextMuted"
    )

    val glassThemeColors = GlassThemeColors(
        cardBackground = animatedCardBg,
        cardBorderTop = animatedBorderTop,
        cardBorderBottom = animatedBorderBottom,
        textPrimary = animatedTextPrimary,
        textSecondary = animatedTextSecondary,
        textMuted = animatedTextMuted,
        iconTint = animatedTextPrimary,
        isDark = isDark
    )

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = Color.Transparent.toArgb()
                it.navigationBarColor = Color.Transparent.toArgb()
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = !isDark
                WindowCompat.getInsetsController(it, view).isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    CompositionLocalProvider(LocalGlassColors provides glassThemeColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
