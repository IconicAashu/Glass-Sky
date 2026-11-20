package com.glasssky.weather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glasssky.weather.data.model.CurrentWeather
import com.glasssky.weather.data.model.WeatherConditionMapper
import com.glasssky.weather.ui.theme.LocalGlassColors
import kotlin.math.roundToInt

@Composable
fun WeatherMetricsGrid(
    current: CurrentWeather,
    precipitationChance: Int,
    isMetric: Boolean,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current
    val (uvLabel, uvColor) = WeatherConditionMapper.getUvLevel(current.uvIndex)
    val windDirText = WeatherConditionMapper.getWindDirection(current.windDirection)

    val windSpeedText = if (isMetric) {
        "${current.windSpeed.roundToInt()} km/h"
    } else {
        "${(current.windSpeed * 0.621371).roundToInt()} mph"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Row 1: Wind & Humidity
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "WIND",
                icon = Icons.Default.Air,
                value = windSpeedText,
                subtitle = "From $windDirText",
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = null,
                    tint = glassColors.textSecondary,
                    modifier = Modifier
                        .size(16.dp)
                        .rotate(current.windDirection.toFloat())
                )
            }

            MetricCard(
                title = "HUMIDITY",
                icon = Icons.Default.WaterDrop,
                value = "${current.relativeHumidity}%",
                subtitle = if (current.relativeHumidity > 65) "High moisture" else "Comfortable",
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: UV Index & Precipitation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "UV INDEX",
                icon = Icons.Default.WbSunny,
                value = "${(current.uvIndex * 10).roundToInt() / 10.0}",
                subtitle = uvLabel,
                modifier = Modifier.weight(1f),
                badge = {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(uvColor.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = uvLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = uvColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )

            MetricCard(
                title = "PRECIPITATION",
                icon = Icons.Default.Grain,
                value = "${precipitationChance}%",
                subtitle = if (current.precipitation > 0) "${current.precipitation} mm rain" else "No precipitation",
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3: Sunrise & Sunset
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BrightnessMedium,
                        contentDescription = null,
                        tint = glassColors.textMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SUNRISE & SUNSET",
                        style = MaterialTheme.typography.labelSmall,
                        color = glassColors.textMuted,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Sunrise",
                            style = MaterialTheme.typography.labelMedium,
                            color = glassColors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = current.sunrise,
                            style = MaterialTheme.typography.headlineMedium,
                            color = glassColors.textPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(glassColors.textMuted.copy(alpha = 0.25f))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Sunset",
                            style = MaterialTheme.typography.labelMedium,
                            color = glassColors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = current.sunset,
                            style = MaterialTheme.typography.headlineMedium,
                            color = glassColors.textPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    icon: ImageVector,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    badge: (@Composable () -> Unit)? = null,
    extraIcon: (@Composable () -> Unit)? = null
) {
    val glassColors = LocalGlassColors.current

    GlassCard(
        modifier = modifier,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = glassColors.textMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        color = glassColors.textMuted,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                }
                extraIcon?.invoke()
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = glassColors.textPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = glassColors.textSecondary
                )
                badge?.invoke()
            }
        }
    }
}
