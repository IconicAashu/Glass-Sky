package com.glasssky.weather.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.glasssky.weather.data.model.AtmosphereType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-fidelity 3D Volumetric Weather Illustration Engine.
 * Matches the reference image's stunning 3D sun, puffy white cloud, and glossy raindrop capsules.
 */
@Composable
fun WeatherIllustration3D(
    atmosphere: AtmosphereType,
    isDay: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 190.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Illustration3DAnim")

    // Gentle floating bobbing for hero illustration
    val floatOffsetY by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatBobbing"
    )

    // Animated raindrop falling phase
    val rainDropPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RainDropFall"
    )

    // Gentle sun flare / breathing scale
    val sunPulse by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SunPulse"
    )

    // Lightning flash pulse for thunderstorms
    val lightningFlash by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3000
                0.4f at 0
                0.4f at 2200
                1.0f at 2300
                0.5f at 2400
                1.0f at 2500
                0.4f at 2650
                0.4f at 3000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "LightningFlash"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            when (atmosphere) {
                AtmosphereType.CLEAR_DAY -> {
                    draw3DSunFull(w, h, floatOffsetY, sunPulse)
                }
                AtmosphereType.CLEAR_NIGHT -> {
                    draw3DMoonFull(w, h, floatOffsetY)
                }
                AtmosphereType.PARTLY_CLOUDY_DAY -> {
                    draw3DSun(w, h, floatOffsetY, sunPulse, offsetX = 0.10f, offsetY = -0.06f)
                    draw3DCloud(w, h, floatOffsetY)
                }
                AtmosphereType.PARTLY_CLOUDY_NIGHT -> {
                    draw3DMoon(w, h, floatOffsetY, offsetX = 0.12f, offsetY = -0.06f)
                    draw3DCloud(w, h, floatOffsetY)
                }
                AtmosphereType.RAINY -> {
                    if (isDay) {
                        draw3DSun(w, h, floatOffsetY, sunPulse, offsetX = 0.08f, offsetY = -0.10f)
                    } else {
                        draw3DMoon(w, h, floatOffsetY, offsetX = 0.10f, offsetY = -0.10f)
                    }
                    draw3DRaindrops(w, h, floatOffsetY, rainDropPhase)
                    draw3DCloud(w, h, floatOffsetY)
                }
                AtmosphereType.THUNDERSTORM -> {
                    draw3DLightning(w, h, floatOffsetY, lightningFlash)
                    draw3DRaindrops(w, h, floatOffsetY, rainDropPhase)
                    draw3DStormCloud(w, h, floatOffsetY)
                }
                AtmosphereType.SNOWY -> {
                    draw3DSnowflakes(w, h, floatOffsetY, rainDropPhase)
                    draw3DCloud(w, h, floatOffsetY)
                }
                AtmosphereType.OVERCAST -> {
                    draw3DCloud(w, h, floatOffsetY + 6f, scale = 0.85f, alpha = 0.7f, offsetY = -0.06f)
                    draw3DCloud(w, h, floatOffsetY)
                }
                AtmosphereType.FOGGY -> {
                    draw3DCloud(w, h, floatOffsetY)
                    draw3DMistLines(w, h, floatOffsetY)
                }
            }
        }
    }
}

/**
 * Miniature 3D Weather Illustration for Hourly Forecast and Daily Forecast cards.
 */
@Composable
fun MiniWeatherIllustration3D(
    atmosphere: AtmosphereType,
    isDay: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 38.dp
) {
    WeatherIllustration3D(
        atmosphere = atmosphere,
        isDay = isDay,
        modifier = modifier,
        size = size
    )
}

// -------------------------------------------------------------
// 3D DRAWING PRIMITIVES
// -------------------------------------------------------------

/**
 * 3D Sun Sphere peeking behind clouds (matches reference image).
 */
private fun DrawScope.draw3DSun(
    w: Float,
    h: Float,
    floatY: Float,
    pulse: Float,
    offsetX: Float = 0.08f,
    offsetY: Float = -0.08f
) {
    val radius = w * 0.26f * pulse
    val centerX = w * (0.50f + offsetX)
    val centerY = h * (0.38f + offsetY) + floatY

    // Outer warm radial glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFF9E22).copy(alpha = 0.45f),
                Color(0xFFFF7A00).copy(alpha = 0.15f),
                Color.Transparent
            ),
            center = Offset(centerX, centerY),
            radius = radius * 1.55f
        ),
        radius = radius * 1.55f,
        center = Offset(centerX, centerY)
    )

    // Volumetric 3D Sun Sphere
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFF7C2), // Specular light highlight
                Color(0xFFFFC043), // Golden amber midtone
                Color(0xFFFF7A00), // Vibrant orange
                Color(0xFFE64A00)  // Deep warm sphere edge
            ),
            center = Offset(centerX - radius * 0.28f, centerY - radius * 0.28f),
            radius = radius * 1.15f
        ),
        radius = radius,
        center = Offset(centerX, centerY)
    )
}

/**
 * Full prominent 3D Sun for clear sunny skies.
 */
private fun DrawScope.draw3DSunFull(
    w: Float,
    h: Float,
    floatY: Float,
    pulse: Float
) {
    val radius = w * 0.30f * pulse
    val centerX = w * 0.50f
    val centerY = h * 0.46f + floatY

    // Atmospheric warm outer glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFAB26).copy(alpha = 0.48f),
                Color(0xFFFF7800).copy(alpha = 0.20f),
                Color.Transparent
            ),
            center = Offset(centerX, centerY),
            radius = radius * 1.65f
        ),
        radius = radius * 1.65f,
        center = Offset(centerX, centerY)
    )

    // 3D Sun Sphere
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFF9D9),
                Color(0xFFFFC44D),
                Color(0xFFFF7F0A),
                Color(0xFFE84D00)
            ),
            center = Offset(centerX - radius * 0.30f, centerY - radius * 0.30f),
            radius = radius * 1.20f
        ),
        radius = radius,
        center = Offset(centerX, centerY)
    )
}

/**
 * 3D Luminous Crescent Moon.
 */
private fun DrawScope.draw3DMoon(
    w: Float,
    h: Float,
    floatY: Float,
    offsetX: Float = 0.12f,
    offsetY: Float = -0.06f
) {
    val radius = w * 0.20f
    val centerX = w * (0.50f + offsetX)
    val centerY = h * (0.36f + offsetY) + floatY

    // Celestial glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF93C5FD).copy(alpha = 0.4f),
                Color(0xFF3B82F6).copy(alpha = 0.12f),
                Color.Transparent
            ),
            center = Offset(centerX, centerY),
            radius = radius * 1.6f
        ),
        radius = radius * 1.6f,
        center = Offset(centerX, centerY)
    )

    // Golden/silvery crescent moon
    val moonPath = Path().apply {
        addOval(androidx.compose.ui.geometry.Rect(centerX - radius, centerY - radius, centerX + radius, centerY + radius))
    }
    drawPath(
        path = moonPath,
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFF3C4), Color(0xFFFDE047), Color(0xFFEAB308)),
            center = Offset(centerX - radius * 0.2f, centerY - radius * 0.2f),
            radius = radius
        )
    )
}

/**
 * Full prominent 3D Crescent Moon for clear night.
 */
private fun DrawScope.draw3DMoonFull(
    w: Float,
    h: Float,
    floatY: Float
) {
    val radius = w * 0.26f
    val centerX = w * 0.50f
    val centerY = h * 0.46f + floatY

    // Soft celestial glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF93C5FD).copy(alpha = 0.45f),
                Color(0xFF60A5FA).copy(alpha = 0.18f),
                Color.Transparent
            ),
            center = Offset(centerX, centerY),
            radius = radius * 1.8f
        ),
        radius = radius * 1.8f,
        center = Offset(centerX, centerY)
    )

    // Volumetric Crescent
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFFBEB), Color(0xFFFDE68A), Color(0xFFF59E0B)),
            center = Offset(centerX - radius * 0.25f, centerY - radius * 0.25f),
            radius = radius * 1.1f
        ),
        radius = radius,
        center = Offset(centerX, centerY)
    )

    // Crescent cutout shadow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF0F172A), Color(0xFF0A0E1A)),
            center = Offset(centerX + radius * 0.35f, centerY - radius * 0.30f),
            radius = radius * 0.95f
        ),
        radius = radius * 0.88f,
        center = Offset(centerX + radius * 0.36f, centerY - radius * 0.28f)
    )
}

/**
 * Volumetric 3D Puffy White Cloud (matches the reference image).
 */
private fun DrawScope.draw3DCloud(
    w: Float,
    h: Float,
    floatY: Float,
    scale: Float = 1.0f,
    alpha: Float = 1.0f,
    offsetY: Float = 0f
) {
    val cloudWidth = w * 0.68f * scale
    val cloudHeight = h * 0.34f * scale
    val cloudLeft = (w - cloudWidth) / 2f
    val cloudTop = h * (0.34f + offsetY) + floatY

    // Soft ambient drop shadow underneath cloud for 3D realism
    drawRoundRect(
        color = Color(0x2E000000),
        topLeft = Offset(cloudLeft + cloudWidth * 0.08f, cloudTop + cloudHeight * 0.42f),
        size = Size(cloudWidth * 0.84f, cloudHeight * 0.58f),
        cornerRadius = CornerRadius(cloudHeight * 0.30f, cloudHeight * 0.30f)
    )

    // Multi-lobed cloud gradient brush (subtle top-light specular highlight to soft bottom shadow)
    val cloudBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFFFF).copy(alpha = alpha),
            Color(0xFFF8FAFD).copy(alpha = alpha),
            Color(0xFFE4EDF7).copy(alpha = alpha)
        ),
        startY = cloudTop,
        endY = cloudTop + cloudHeight
    )

    // Lobe 1: Base rounded pillow
    drawRoundRect(
        brush = cloudBrush,
        topLeft = Offset(cloudLeft, cloudTop + cloudHeight * 0.30f),
        size = Size(cloudWidth, cloudHeight * 0.65f),
        cornerRadius = CornerRadius(cloudHeight * 0.34f, cloudHeight * 0.34f)
    )

    // Lobe 2: Left puff
    val leftLobeRadius = cloudHeight * 0.42f
    drawCircle(
        brush = cloudBrush,
        radius = leftLobeRadius,
        center = Offset(cloudLeft + cloudWidth * 0.25f, cloudTop + cloudHeight * 0.46f)
    )

    // Lobe 3: Center-top dominant puff
    val centerLobeRadius = cloudHeight * 0.52f
    drawCircle(
        brush = cloudBrush,
        radius = centerLobeRadius,
        center = Offset(cloudLeft + cloudWidth * 0.52f, cloudTop + cloudHeight * 0.34f)
    )

    // Lobe 4: Right puff
    val rightLobeRadius = cloudHeight * 0.38f
    drawCircle(
        brush = cloudBrush,
        radius = rightLobeRadius,
        center = Offset(cloudLeft + cloudWidth * 0.78f, cloudTop + cloudHeight * 0.48f)
    )

    // Subtle 3D Top Specular Highlight rim
    val rimBrush = Brush.horizontalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0f),
            Color.White.copy(alpha = 0.65f * alpha),
            Color.White.copy(alpha = 0f)
        )
    )
    drawRoundRect(
        brush = rimBrush,
        topLeft = Offset(cloudLeft + cloudWidth * 0.20f, cloudTop + cloudHeight * 0.05f),
        size = Size(cloudWidth * 0.55f, 4f),
        cornerRadius = CornerRadius(2f, 2f)
    )
}

/**
 * Dark Storm Cloud for thunderstorms.
 */
private fun DrawScope.draw3DStormCloud(
    w: Float,
    h: Float,
    floatY: Float
) {
    val cloudWidth = w * 0.68f
    val cloudHeight = h * 0.34f
    val cloudLeft = (w - cloudWidth) / 2f
    val cloudTop = h * 0.34f + floatY

    val stormBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF64748B),
            Color(0xFF334155),
            Color(0xFF1E293B)
        ),
        startY = cloudTop,
        endY = cloudTop + cloudHeight
    )

    // Base
    drawRoundRect(
        brush = stormBrush,
        topLeft = Offset(cloudLeft, cloudTop + cloudHeight * 0.30f),
        size = Size(cloudWidth, cloudHeight * 0.65f),
        cornerRadius = CornerRadius(cloudHeight * 0.34f, cloudHeight * 0.34f)
    )
    // Left
    drawCircle(
        brush = stormBrush,
        radius = cloudHeight * 0.42f,
        center = Offset(cloudLeft + cloudWidth * 0.25f, cloudTop + cloudHeight * 0.46f)
    )
    // Center
    drawCircle(
        brush = stormBrush,
        radius = cloudHeight * 0.52f,
        center = Offset(cloudLeft + cloudWidth * 0.52f, cloudTop + cloudHeight * 0.34f)
    )
    // Right
    drawCircle(
        brush = stormBrush,
        radius = cloudHeight * 0.38f,
        center = Offset(cloudLeft + cloudWidth * 0.78f, cloudTop + cloudHeight * 0.48f)
    )
}

/**
 * 3D Glossy Raindrop Capsules (matches the exact 4-capsule drops in the reference image).
 */
private fun DrawScope.draw3DRaindrops(
    w: Float,
    h: Float,
    floatY: Float,
    phase: Float
) {
    // 4 rounded capsule raindrop coordinates matching reference
    val dropWidth = w * 0.038f
    val dropHeight = h * 0.10f

    // Relative X positions across the cloud
    val dropXCoords = listOf(0.38f, 0.46f, 0.54f, 0.62f)
    // Staggered baseY offsets
    val baseYOffsets = listOf(0.64f, 0.60f, 0.62f, 0.67f)
    // Staggered phase deltas for natural cascade
    val phaseOffsets = listOf(0.0f, 0.25f, 0.50f, 0.75f)

    for (i in 0 until 4) {
        val dropPhase = (phase + phaseOffsets[i]) % 1f
        // Smooth bobbing / downward travel
        val animOffsetY = sin(dropPhase * 2 * PI).toFloat() * (h * 0.03f)

        val x = w * dropXCoords[i]
        val y = h * baseYOffsets[i] + floatY + animOffsetY

        // Vibrant 3D Capsule Pill
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF38BDF8), // Cyan sky blue highlight
                    Color(0xFF2563EB), // Vibrant blue
                    Color(0xFF1D4ED8)  // Deep royal blue base
                ),
                startY = y,
                endY = y + dropHeight
            ),
            topLeft = Offset(x, y),
            size = Size(dropWidth, dropHeight),
            cornerRadius = CornerRadius(dropWidth / 2f, dropWidth / 2f)
        )

        // Glossy Specular Highlight Dot on top of capsule
        drawCircle(
            color = Color.White.copy(alpha = 0.75f),
            radius = dropWidth * 0.24f,
            center = Offset(x + dropWidth * 0.5f, y + dropWidth * 0.6f)
        )
    }
}

/**
 * 3D Golden Zigzag Lightning Bolt.
 */
private fun DrawScope.draw3DLightning(
    w: Float,
    h: Float,
    floatY: Float,
    flashAlpha: Float
) {
    val startX = w * 0.50f
    val startY = h * 0.58f + floatY

    val boltPath = Path().apply {
        moveTo(startX, startY)
        lineTo(startX - w * 0.06f, startY + h * 0.11f)
        lineTo(startX + w * 0.01f, startY + h * 0.11f)
        lineTo(startX - w * 0.08f, startY + h * 0.24f)
        lineTo(startX + w * 0.04f, startY + h * 0.13f)
        lineTo(startX - w * 0.02f, startY + h * 0.13f)
        close()
    }

    // Outer flash glow
    drawPath(
        path = boltPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFEF08A).copy(alpha = flashAlpha),
                Color(0xFFFACC15).copy(alpha = flashAlpha),
                Color(0xFFEAB308).copy(alpha = flashAlpha)
            )
        )
    )
}

/**
 * 3D Soft Volumetric Snowflakes.
 */
private fun DrawScope.draw3DSnowflakes(
    w: Float,
    h: Float,
    floatY: Float,
    phase: Float
) {
    val snowCoords = listOf(
        Pair(0.36f, 0.66f),
        Pair(0.48f, 0.62f),
        Pair(0.58f, 0.68f),
        Pair(0.42f, 0.76f),
        Pair(0.54f, 0.78f)
    )

    snowCoords.forEachIndexed { idx, (px, py) ->
        val flakePhase = (phase + idx * 0.2f) % 1f
        val animY = (flakePhase * h * 0.08f)
        val cx = w * px + sin(flakePhase * 2 * PI).toFloat() * 6f
        val cy = h * py + floatY + animY
        val r = w * 0.022f

        // Soft white snowflake sphere
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White, Color(0xFFE2E8F0)),
                center = Offset(cx - r * 0.3f, cy - r * 0.3f),
                radius = r
            ),
            radius = r,
            center = Offset(cx, cy)
        )
    }
}

/**
 * Horizontal Mist Streamlines.
 */
private fun DrawScope.draw3DMistLines(
    w: Float,
    h: Float,
    floatY: Float
) {
    val mistYPositions = listOf(0.68f, 0.75f, 0.82f)
    mistYPositions.forEachIndexed { i, yRatio ->
        val y = h * yRatio + floatY
        val lineWidth = w * (0.50f + i * 0.08f)
        val startX = (w - lineWidth) / 2f

        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = 0.55f),
                    Color.Transparent
                )
            ),
            start = Offset(startX, y),
            end = Offset(startX + lineWidth, y),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}
