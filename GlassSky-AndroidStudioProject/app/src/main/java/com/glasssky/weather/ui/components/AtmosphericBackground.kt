package com.glasssky.weather.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.glasssky.weather.data.model.AtmosphereType
import com.glasssky.weather.ui.theme.LocalGlassColors

@Composable
fun AtmosphericBackground(
    atmosphere: AtmosphereType,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isDark = LocalGlassColors.current.isDark

    // Base background gradient stops (Midnight Charcoal/Slate matching reference)
    val (targetTop, targetMid, targetBottom) = if (isDark) {
        when (atmosphere) {
            AtmosphereType.CLEAR_DAY -> Triple(Color(0xFF0D111A), Color(0xFF111726), Color(0xFF161F33))
            AtmosphereType.CLEAR_NIGHT -> Triple(Color(0xFF060911), Color(0xFF0A0F1D), Color(0xFF111728))
            AtmosphereType.PARTLY_CLOUDY_DAY -> Triple(Color(0xFF0E131E), Color(0xFF131A2A), Color(0xFF1B243B))
            AtmosphereType.PARTLY_CLOUDY_NIGHT -> Triple(Color(0xFF060911), Color(0xFF0B101E), Color(0xFF131A2E))
            AtmosphereType.OVERCAST -> Triple(Color(0xFF0C101A), Color(0xFF121724), Color(0xFF1A2133))
            AtmosphereType.FOGGY -> Triple(Color(0xFF0F1117), Color(0xFF141720), Color(0xFF1D212E))
            AtmosphereType.RAINY -> Triple(Color(0xFF0A0E18), Color(0xFF101625), Color(0xFF172036))
            AtmosphereType.SNOWY -> Triple(Color(0xFF0B101D), Color(0xFF12192D), Color(0xFF1B2540))
            AtmosphereType.THUNDERSTORM -> Triple(Color(0xFF0B0A17), Color(0xFF130E26), Color(0xFF1E143B))
        }
    } else {
        when (atmosphere) {
            AtmosphereType.CLEAR_DAY -> Triple(Color(0xFF38BDF8), Color(0xFF7DD3FC), Color(0xFFE0F2FE))
            AtmosphereType.CLEAR_NIGHT -> Triple(Color(0xFF334155), Color(0xFF475569), Color(0xFF94A3B8))
            AtmosphereType.PARTLY_CLOUDY_DAY -> Triple(Color(0xFF60A5FA), Color(0xFF93C5FD), Color(0xFFE2E8F0))
            AtmosphereType.PARTLY_CLOUDY_NIGHT -> Triple(Color(0xFF475569), Color(0xFF64748B), Color(0xFFCBD5E1))
            AtmosphereType.OVERCAST -> Triple(Color(0xFF94A3B8), Color(0xFFCBD5E1), Color(0xFFF1F5F9))
            AtmosphereType.FOGGY -> Triple(Color(0xFFA1A1AA), Color(0xFFD4D4D8), Color(0xFFF4F4F5))
            AtmosphereType.RAINY -> Triple(Color(0xFF64748B), Color(0xFF94A3B8), Color(0xFFE2E8F0))
            AtmosphereType.SNOWY -> Triple(Color(0xFF93C5FD), Color(0xFFBFDBFE), Color(0xFFF8FAFC))
            AtmosphereType.THUNDERSTORM -> Triple(Color(0xFF6B21A8), Color(0xFF8B5CF6), Color(0xFFDDD6FE))
        }
    }

    // Atmospheric Warm/Cool Radial Aura directly behind Hero Weather 3D illustration
    val targetAuraColor = if (isDark) {
        when (atmosphere) {
            AtmosphereType.CLEAR_DAY -> Color(0xFFE27C22).copy(alpha = 0.52f)
            AtmosphereType.CLEAR_NIGHT -> Color(0xFF38BDF8).copy(alpha = 0.28f)
            AtmosphereType.PARTLY_CLOUDY_DAY -> Color(0xFFE07A24).copy(alpha = 0.45f)
            AtmosphereType.PARTLY_CLOUDY_NIGHT -> Color(0xFF60A5FA).copy(alpha = 0.25f)
            AtmosphereType.OVERCAST -> Color(0xFF475569).copy(alpha = 0.30f)
            AtmosphereType.FOGGY -> Color(0xFF52525B).copy(alpha = 0.30f)
            AtmosphereType.RAINY -> Color(0xFFDE731D).copy(alpha = 0.50f) // Warm amber sunset glow behind rain cloud (reference match!)
            AtmosphereType.SNOWY -> Color(0xFF38BDF8).copy(alpha = 0.35f)
            AtmosphereType.THUNDERSTORM -> Color(0xFF7C3AED).copy(alpha = 0.45f)
        }
    } else {
        Color.White.copy(alpha = 0.35f)
    }

    val topColor by animateColorAsState(targetValue = targetTop, animationSpec = tween(700), label = "TopGrad")
    val midColor by animateColorAsState(targetValue = targetMid, animationSpec = tween(700), label = "MidGrad")
    val bottomColor by animateColorAsState(targetValue = targetBottom, animationSpec = tween(700), label = "BotGrad")
    val auraColor by animateColorAsState(targetValue = targetAuraColor, animationSpec = tween(700), label = "AuraColor")

    Box(modifier = modifier.fillMaxSize()) {
        // Base Atmospheric Gradient
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Deep Midnight/Atmospheric Linear Gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(topColor, midColor, bottomColor),
                    startY = 0f,
                    endY = h
                )
            )

            // 2. Atmospheric Radial Glow behind 3D Hero Illustration (approx y = 35% of screen)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        auraColor,
                        auraColor.copy(alpha = auraColor.alpha * 0.45f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.50f, h * 0.34f),
                    radius = w * 0.72f
                ),
                radius = w * 0.72f,
                center = Offset(w * 0.50f, h * 0.34f)
            )
        }

        // Live Dynamic Weather Particles (Stars, Rain, Thunder, Snow, Drifting Clouds)
        WeatherParticlesCanvas(atmosphere = atmosphere)

        content()
    }
}
