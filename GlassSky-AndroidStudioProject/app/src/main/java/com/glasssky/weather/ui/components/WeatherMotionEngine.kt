package com.glasssky.weather.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.glasssky.weather.data.model.AtmosphereType
import kotlin.math.sin

/**
 * High-performance, battery-efficient weather particle motion engine.
 * Uses Compose InfiniteTransition with zero allocation inside draw loops.
 */
@Composable
fun WeatherParticlesCanvas(
    atmosphere: AtmosphereType,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WeatherMotionLoop")

    // Continuous 0f..1f loop over 3 seconds for particle motion
    val motionProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "MotionProgress"
    )

    // Lightning flash burst for thunderstorm
    val lightningAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 5000
                0.0f at 0
                0.0f at 3800
                0.7f at 3900
                0.1f at 4000
                0.9f at 4100
                0.0f at 4250
                0.0f at 5000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "LightningFlash"
    )

    // Solar / Moon breathing pulse
    val celestialPulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CelestialPulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        when (atmosphere) {
            AtmosphereType.CLEAR_DAY -> {
                drawSunnyRays(w, h, celestialPulse)
            }
            AtmosphereType.CLEAR_NIGHT -> {
                drawTwinklingStars(w, h, celestialPulse)
            }
            AtmosphereType.PARTLY_CLOUDY_DAY, AtmosphereType.OVERCAST -> {
                drawDriftingClouds(w, h, motionProgress, isNight = false)
            }
            AtmosphereType.PARTLY_CLOUDY_NIGHT -> {
                drawTwinklingStars(w, h, celestialPulse * 0.7f)
                drawDriftingClouds(w, h, motionProgress, isNight = true)
            }
            AtmosphereType.RAINY -> {
                drawRainStreaks(w, h, motionProgress)
            }
            AtmosphereType.THUNDERSTORM -> {
                // Lightning flash backdrop
                if (lightningAlpha > 0.05f) {
                    drawRect(color = Color(0xFFC084FC).copy(alpha = lightningAlpha * 0.25f))
                }
                drawRainStreaks(w, h, motionProgress)
            }
            AtmosphereType.SNOWY -> {
                drawFallingSnow(w, h, motionProgress)
            }
            AtmosphereType.FOGGY -> {
                drawMistyHaze(w, h, celestialPulse)
            }
        }
    }
}

// 1. Sunny Rays and Solar Glow
private fun DrawScope.drawSunnyRays(w: Float, h: Float, pulse: Float) {
    val sunCenter = Offset(w * 0.88f, h * 0.08f)
    drawCircle(
        color = Color(0xFFFDE047).copy(alpha = 0.18f * pulse),
        radius = w * 0.45f * pulse,
        center = sunCenter
    )
    drawCircle(
        color = Color(0xFFFEF08A).copy(alpha = 0.25f),
        radius = w * 0.25f,
        center = sunCenter
    )
    drawCircle(
        color = Color(0xFFFFFBEB).copy(alpha = 0.45f),
        radius = w * 0.12f,
        center = sunCenter
    )
}

// 2. Night Stars (Deterministic pre-calculated positions)
private fun DrawScope.drawTwinklingStars(w: Float, h: Float, twinkle: Float) {
    val starCoords = listOf(
        Pair(0.12f, 0.08f) to 2.2f,
        Pair(0.25f, 0.15f) to 3.0f,
        Pair(0.38f, 0.06f) to 1.8f,
        Pair(0.52f, 0.18f) to 2.6f,
        Pair(0.68f, 0.11f) to 3.2f,
        Pair(0.82f, 0.22f) to 2.0f,
        Pair(0.18f, 0.28f) to 2.4f,
        Pair(0.32f, 0.35f) to 1.6f,
        Pair(0.60f, 0.32f) to 2.8f,
        Pair(0.76f, 0.38f) to 2.2f,
        Pair(0.90f, 0.16f) to 3.4f,
        Pair(0.44f, 0.24f) to 2.0f
    )

    for ((coord, radius) in starCoords) {
        val (rx, ry) = coord
        val alpha = (0.35f + (sin((rx * 10f + twinkle * 3f).toDouble()).toFloat() * 0.35f)).coerceIn(0.1f, 0.85f)
        drawCircle(
            color = Color.White.copy(alpha = alpha),
            radius = radius,
            center = Offset(w * rx, h * ry)
        )
    }

    // Moon Glow
    val moonCenter = Offset(w * 0.25f, h * 0.12f)
    drawCircle(
        color = Color(0xFF93C5FD).copy(alpha = 0.12f * twinkle),
        radius = w * 0.35f,
        center = moonCenter
    )
    drawCircle(
        color = Color(0xFFDBEAFE).copy(alpha = 0.20f),
        radius = w * 0.10f,
        center = moonCenter
    )
}

// 3. Rain Streaks (Lightweight 24 angled lines)
private fun DrawScope.drawRainStreaks(w: Float, h: Float, progress: Float) {
    val rainDropCount = 28
    val streakLength = 45f
    val angleOffset = 8f

    for (i in 0 until rainDropCount) {
        val seed = (i * 37) % 100 / 100f
        val xBase = (w * ((i.toFloat() / rainDropCount) + seed * 0.1f)) % w
        val yBase = ((h * (progress * 1.8f + seed)) % (h + 100f)) - 50f
        val alpha = 0.25f + ((i % 5) * 0.08f)

        drawLine(
            color = Color(0xFFBAE6FD).copy(alpha = alpha),
            start = Offset(xBase, yBase),
            end = Offset(xBase - angleOffset, yBase + streakLength),
            strokeWidth = 1.8f
        )
    }
}

// 4. Falling Snow Flakes (Gently swaying snowflakes)
private fun DrawScope.drawFallingSnow(w: Float, h: Float, progress: Float) {
    val flakeCount = 26
    for (i in 0 until flakeCount) {
        val seed = (i * 43) % 100 / 100f
        val sway = sin((progress * 6.28f + i).toDouble()).toFloat() * 14f
        val x = (w * (i.toFloat() / flakeCount) + sway) % w
        val y = ((h * (progress + seed)) % (h + 40f)) - 20f
        val size = 2.2f + (i % 4) * 0.8f
        val alpha = 0.35f + (i % 3) * 0.2f

        drawCircle(
            color = Color.White.copy(alpha = alpha),
            radius = size,
            center = Offset(x, y)
        )
    }
}

// 5. Drifting Clouds
private fun DrawScope.drawDriftingClouds(w: Float, h: Float, progress: Float, isNight: Boolean) {
    val cloudColor = if (isNight) Color(0xFF1E293B).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.12f)
    val driftX = (progress * w * 0.25f) % w

    // Cloud blob 1
    drawCircle(
        color = cloudColor,
        radius = w * 0.32f,
        center = Offset(w * 0.2f + driftX, h * 0.16f)
    )
    drawCircle(
        color = cloudColor,
        radius = w * 0.40f,
        center = Offset(w * 0.45f + driftX, h * 0.12f)
    )

    // Cloud blob 2
    drawCircle(
        color = cloudColor.copy(alpha = cloudColor.alpha * 0.75f),
        radius = w * 0.36f,
        center = Offset((w * 0.75f - driftX).let { if (it < 0) it + w else it }, h * 0.22f)
    )
}

// 6. Misty Haze
private fun DrawScope.drawMistyHaze(w: Float, h: Float, pulse: Float) {
    drawRect(
        color = Color(0xFFA8A29E).copy(alpha = 0.08f * pulse)
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.06f),
        radius = w * 0.65f,
        center = Offset(w * 0.5f, h * 0.25f)
    )
}
