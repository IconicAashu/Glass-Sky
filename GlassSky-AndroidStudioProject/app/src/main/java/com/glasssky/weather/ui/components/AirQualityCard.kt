package com.glasssky.weather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glasssky.weather.data.model.AirQualityData
import com.glasssky.weather.ui.theme.LocalGlassColors

@Composable
fun AirQualityCard(
    airQuality: AirQualityData?,
    modifier: Modifier = Modifier
) {
    if (airQuality == null) return

    val glassColors = LocalGlassColors.current

    val (badgeBg, badgeTextCol) = when {
        airQuality.aqi <= 50 -> Color(0xFF10B981) to Color.White // Emerald
        airQuality.aqi <= 100 -> Color(0xFFFBBF24) to Color.Black // Amber
        airQuality.aqi <= 150 -> Color(0xFFF97316) to Color.White // Orange
        airQuality.aqi <= 200 -> Color(0xFFEF4444) to Color.White // Red
        airQuality.aqi <= 300 -> Color(0xFF8B5CF6) to Color.White // Purple
        else -> Color(0xFF7F1D1D) to Color.White // Maroon
    }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Air,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "AIR QUALITY INDEX",
                        style = MaterialTheme.typography.labelSmall,
                        color = glassColors.textMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                // AQI score badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = badgeBg.copy(alpha = 0.22f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, badgeBg.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(badgeBg)
                        )
                        Text(
                            text = "${airQuality.aqi} • ${airQuality.category}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (glassColors.isDark) Color.White else Color(0xFF0F172A)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Gauge progress bar
            val gaugeFraction = (airQuality.aqi / 300f).coerceIn(0.04f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF10B981),
                                Color(0xFFFBBF24),
                                Color(0xFFF97316),
                                Color(0xFFEF4444),
                                Color(0xFF8B5CF6),
                                Color(0xFF7F1D1D)
                            )
                        )
                    )
            ) {
                // Indicator mark
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(gaugeFraction)
                        .padding(end = 2.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp, 10.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.5.dp, Color.Black.copy(alpha = 0.6f), CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Health Advisory description
            Text(
                text = airQuality.healthAdvice,
                style = MaterialTheme.typography.bodyMedium,
                color = glassColors.textSecondary,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Pollutant Breakdown Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PollutantChip(label = "PM2.5", value = "${airQuality.pm25} µg")
                PollutantChip(label = "PM10", value = "${airQuality.pm10} µg")
                PollutantChip(label = "O₃", value = "${airQuality.ozone} µg")
                PollutantChip(label = "NO₂", value = "${airQuality.no2} µg")
            }
        }
    }
}

@Composable
private fun PollutantChip(
    label: String,
    value: String
) {
    val glassColors = LocalGlassColors.current
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (glassColors.isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = glassColors.textMuted,
                fontSize = 11.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = glassColors.textPrimary,
                fontSize = 12.sp
            )
        }
    }
}
