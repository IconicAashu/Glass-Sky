package com.glasssky.weather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.glasssky.weather.ui.theme.LocalGlassColors

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    borderWidth: Dp = 1.dp,
    elevation: Dp = 6.dp,
    customBackground: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val glassColors = LocalGlassColors.current

    val backgroundBrush = if (customBackground != null) {
        Brush.verticalGradient(listOf(customBackground, customBackground))
    } else {
        if (glassColors.isDark) {
            Brush.verticalGradient(
                0.0f to Color.White.copy(alpha = 0.09f),
                0.5f to Color.White.copy(alpha = 0.05f),
                1.0f to Color.White.copy(alpha = 0.025f)
            )
        } else {
            Brush.verticalGradient(
                0.0f to Color.White.copy(alpha = 0.85f),
                0.5f to Color.White.copy(alpha = 0.70f),
                1.0f to Color.White.copy(alpha = 0.55f)
            )
        }
    }

    // Directional specular border highlight (fading downwards)
    val borderGradient = Brush.verticalGradient(
        0.0f to glassColors.cardBorderTop,
        0.5f to glassColors.cardBorderTop.copy(alpha = glassColors.cardBorderTop.alpha * 0.5f),
        1.0f to glassColors.cardBorderBottom
    )

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = if (glassColors.isDark) Color.Black.copy(alpha = 0.25f) else Color(0xFF64748B).copy(alpha = 0.15f),
                spotColor = if (glassColors.isDark) Color.Black.copy(alpha = 0.35f) else Color(0xFF64748B).copy(alpha = 0.20f)
            )
            .clip(shape)
            .background(brush = backgroundBrush)
            .border(
                width = borderWidth,
                brush = borderGradient,
                shape = shape
            ),
        content = content
    )
}
