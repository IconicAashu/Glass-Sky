package com.glasssky.weather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glasssky.weather.data.model.HourlyItem
import com.glasssky.weather.data.model.WeatherConditionMapper
import com.glasssky.weather.ui.theme.LocalGlassColors
import kotlin.math.roundToInt

@Composable
fun HourlyForecastRow(
    hourlyList: List<HourlyItem>,
    isCelsius: Boolean,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Section Header matching reference: "🕒 Hourly Forecast"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(start = 22.dp, bottom = 14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = glassColors.textPrimary.copy(alpha = 0.90f),
                modifier = Modifier.size(19.dp)
            )
            Text(
                text = "Hourly Forecast",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = glassColors.textPrimary
            )
        }

        // Horizontal Row of Squircle Cards
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {
            items(
                items = hourlyList,
                key = { it.time }
            ) { item ->
                HourlySquircleCard(item = item, isCelsius = isCelsius)
            }
        }
    }
}

/**
 * Individual Squircle Frosted Card matching reference image:
 * [ Mini 3D Icon ]
 * [ Time (e.g. Now, 5pm) ]
 * [ Temp (e.g. 29°) ]
 */
@Composable
private fun HourlySquircleCard(
    item: HourlyItem,
    isCelsius: Boolean
) {
    val glassColors = LocalGlassColors.current
    val condition = WeatherConditionMapper.getCondition(item.weatherCode, item.isDay)
    val isNow = item.formattedTime.equals("Now", ignoreCase = true)

    val tempFormatted = if (isCelsius) {
        "${item.temperature.roundToInt()}°"
    } else {
        "${((item.temperature * 9 / 5) + 32).roundToInt()}°"
    }

    val cardShape = RoundedCornerShape(22.dp)
    val cardBackground = if (glassColors.isDark) {
        if (isNow) Color(0xFF1E2535) else Color(0xFF141923).copy(alpha = 0.88f)
    } else {
        if (isNow) Color.White.copy(alpha = 0.95f) else Color.White.copy(alpha = 0.75f)
    }

    val borderColor = if (isNow) {
        Color.White.copy(alpha = 0.40f)
    } else {
        Color.White.copy(alpha = 0.12f)
    }

    Box(
        modifier = Modifier
            .width(76.dp)
            .height(126.dp)
            .clip(cardShape)
            .background(cardBackground)
            .border(1.dp, borderColor, cardShape)
            .padding(vertical = 12.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            // 1. Mini 3D Weather Illustration + Optional Rain %
            Box(contentAlignment = Alignment.Center) {
                MiniWeatherIllustration3D(
                    atmosphere = condition.atmosphere,
                    isDay = item.isDay,
                    size = 36.dp
                )
            }

            if (item.precipitationProbability > 0) {
                Text(
                    text = "${item.precipitationProbability}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF38BDF8)
                )
            } else {
                Spacer(modifier = Modifier.height(2.dp))
            }

            // 2. Time label ("Now", "5pm", "6pm", etc.)
            Text(
                text = item.formattedTime,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = if (isNow) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (isNow) glassColors.textPrimary else glassColors.textSecondary
            )

            // 3. Temperature ("29°", "28°", etc.)
            Text(
                text = tempFormatted,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = glassColors.textPrimary
            )
        }
    }
}
