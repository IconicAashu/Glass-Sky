package com.glasssky.weather.ui.screens.map

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glasssky.weather.data.model.WeatherLocation
import com.glasssky.weather.ui.components.AtmosphericBackground
import com.glasssky.weather.ui.theme.LocalGlassColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class MapLayer {
    PRECIPITATION,
    WIND,
    TEMPERATURE
}

@Composable
fun WeatherMapScreen(
    location: WeatherLocation,
    currentTemp: Double,
    isCelsius: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current
    var selectedLayer by remember { mutableStateOf(MapLayer.PRECIPITATION) }
    var isPlaying by remember { mutableStateOf(true) }
    var timelineStep by remember { mutableStateOf(4) } // 0..6 (Now is 4)

    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
    val radarAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarAngle"
    )

    val radarPulse by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarPulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
    ) {
        // Radar Simulation Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val maxR = size.width.coerceAtLeast(size.height) * 0.65f

            // 1. Base grid lines and range rings
            val ringCount = 5
            for (i in 1..ringCount) {
                val r = (maxR / ringCount) * i
                drawCircle(
                    color = Color(0x1A38BDF8),
                    radius = r,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Crosshairs
            drawLine(
                color = Color(0x1438BDF8),
                start = Offset(cx, cy - maxR),
                end = Offset(cx, cy + maxR),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = Color(0x1438BDF8),
                start = Offset(cx - maxR, cy),
                end = Offset(cx + maxR, cy),
                strokeWidth = 1.dp.toPx()
            )

            // 2. Animated Precipitation Doppler Cells
            if (selectedLayer == MapLayer.PRECIPITATION) {
                // Expanding radar pulse ring
                drawCircle(
                    color = Color(0xFF38BDF8).copy(alpha = (1f - radarPulse) * 0.4f),
                    radius = maxR * radarPulse,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2.dp.toPx())
                )

                // Simulated rain blobs around center
                val rainEchoes = listOf(
                    Triple(cx - 80f, cy - 120f, 90f),
                    Triple(cx + 110f, cy - 50f, 130f),
                    Triple(cx + 40f, cy + 100f, 110f),
                    Triple(cx - 120f, cy + 80f, 85f)
                )

                rainEchoes.forEach { (ex, ey, er) ->
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x8038BDF8),
                                Color(0x4D2563EB),
                                Color.Transparent
                            ),
                            center = Offset(ex, ey),
                            radius = er
                        ),
                        radius = er,
                        center = Offset(ex, ey)
                    )
                }

                // Rotating Doppler beam
                val rad = Math.toRadians(radarAngle.toDouble())
                val endX = cx + maxR * cos(rad).toFloat()
                val endY = cy + maxR * sin(rad).toFloat()
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF38BDF8).copy(alpha = 0.6f), Color.Transparent),
                        start = Offset(cx, cy),
                        end = Offset(endX, endY)
                    ),
                    start = Offset(cx, cy),
                    end = Offset(endX, endY),
                    strokeWidth = 2.dp.toPx()
                )
            } else if (selectedLayer == MapLayer.TEMPERATURE) {
                // Thermal gradient simulation
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x66F97316),
                            Color(0x44FBBF24),
                            Color(0x2238BDF8),
                            Color.Transparent
                        ),
                        center = Offset(cx, cy),
                        radius = maxR
                    ),
                    radius = maxR,
                    center = Offset(cx, cy)
                )
            } else {
                // Wind streamlines simulation
                for (step in 0..8) {
                    val angleOffset = step * 40f + (radarAngle * 0.5f)
                    val rad = Math.toRadians(angleOffset.toDouble())
                    val wx = cx + (maxR * 0.6f) * cos(rad).toFloat()
                    val wy = cy + (maxR * 0.6f) * sin(rad).toFloat()
                    drawLine(
                        color = Color(0x66A5F3FC),
                        start = Offset(wx, wy),
                        end = Offset(wx + 24f, wy - 18f),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }

            // 3. User Location Center Pin
            drawCircle(
                color = Color(0xFF38BDF8),
                radius = 8.dp.toPx(),
                center = Offset(cx, cy)
            )
            drawCircle(
                color = Color.White,
                radius = 4.dp.toPx(),
                center = Offset(cx, cy)
            )
        }

        // Overlay UI
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF161B26).copy(alpha = 0.85f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Live Weather Radar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${location.name}, ${location.country}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.70f)
                    )
                }

                Box(modifier = Modifier.size(42.dp)) // Balancer
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Layer Selector Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LayerFilterChip(
                    label = "Rain Radar",
                    isSelected = selectedLayer == MapLayer.PRECIPITATION,
                    onClick = { selectedLayer = MapLayer.PRECIPITATION }
                )
                LayerFilterChip(
                    label = "Wind Stream",
                    isSelected = selectedLayer == MapLayer.WIND,
                    onClick = { selectedLayer = MapLayer.WIND }
                )
                LayerFilterChip(
                    label = "Thermal Heat",
                    isSelected = selectedLayer == MapLayer.TEMPERATURE,
                    onClick = { selectedLayer = MapLayer.TEMPERATURE }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Radar Timeline Control Card
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color(0xF2121724),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(28.dp))
                    .shadow(16.dp, RoundedCornerShape(28.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { isPlaying = !isPlaying },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF38BDF8))
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Doppler Loop (Live)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = if (isCelsius) "${currentTemp.toInt()}°C" else "${((currentTemp * 9 / 5) + 32).toInt()}°F",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Timeline Steps
                    val steps = listOf("-45m", "-30m", "-15m", "Now", "+15m", "+30m", "+45m")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        steps.forEachIndexed { index, label ->
                            val isCurrent = index == timelineStep
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCurrent) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.clickable { timelineStep = index }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LayerFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val chipShape = RoundedCornerShape(20.dp)
    val bg = if (isSelected) Color(0xFF38BDF8) else Color(0xFF161B26).copy(alpha = 0.85f)
    val textCol = if (isSelected) Color.White else Color.White.copy(alpha = 0.75f)

    Box(
        modifier = Modifier
            .clip(chipShape)
            .background(bg)
            .border(1.dp, Color.White.copy(alpha = if (isSelected) 0.35f else 0.12f), chipShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = textCol
        )
    }
}
