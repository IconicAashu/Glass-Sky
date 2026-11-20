package com.glasssky.weather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.glasssky.weather.data.model.DailyItem
import com.glasssky.weather.data.model.WeatherConditionMapper
import com.glasssky.weather.ui.theme.LocalGlassColors
import kotlin.math.roundToInt

@Composable
fun DailyForecastList(
    dailyList: List<DailyItem>,
    isCelsius: Boolean,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current

    val minTempOverall = dailyList.minOfOrNull { it.tempMin } ?: 0.0
    val maxTempOverall = dailyList.maxOfOrNull { it.tempMax } ?: 30.0
    val tempRange = (maxTempOverall - minTempOverall).coerceAtLeast(1.0)

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = glassColors.textMuted,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "7-DAY EXTENDED FORECAST",
                    style = MaterialTheme.typography.labelSmall,
                    color = glassColors.textMuted,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }

            dailyList.forEachIndexed { index, item ->
                DailyItemRow(
                    item = item,
                    minOverall = minTempOverall,
                    maxOverall = maxTempOverall,
                    rangeOverall = tempRange,
                    isCelsius = isCelsius
                )
                if (index < dailyList.size - 1) {
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
private fun DailyItemRow(
    item: DailyItem,
    minOverall: Double,
    maxOverall: Double,
    rangeOverall: Double,
    isCelsius: Boolean
) {
    val glassColors = LocalGlassColors.current
    val condition = WeatherConditionMapper.getCondition(item.weatherCode, isDay = true)

    val minFormatted = if (isCelsius) "${item.tempMin.roundToInt()}°" else "${((item.tempMin * 9 / 5) + 32).roundToInt()}°"
    val maxFormatted = if (isCelsius) "${item.tempMax.roundToInt()}°" else "${((item.tempMax * 9 / 5) + 32).roundToInt()}°"

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Day name
        Text(
            text = item.dayName,
            style = MaterialTheme.typography.bodyMedium,
            color = glassColors.textPrimary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(68.dp)
        )

        // 3D Weather Icon + Rain %
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(62.dp)
        ) {
            MiniWeatherIllustration3D(
                atmosphere = condition.atmosphere,
                isDay = true,
                size = 28.dp
            )
            if (item.precipitationProbability > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${item.precipitationProbability}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = Color(0xFF38BDF8),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Min Temp
        Text(
            text = minFormatted,
            style = MaterialTheme.typography.bodyMedium,
            color = glassColors.textMuted,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.width(36.dp)
        )

        // Temperature range gradient bar
        val trackBg = if (glassColors.isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFCBD5E1).copy(alpha = 0.5f)

        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(trackBg)
        ) {
            val startFrac = ((item.tempMin - minOverall) / rangeOverall).toFloat().coerceIn(0f, 1f)
            val endFrac = ((item.tempMax - minOverall) / rangeOverall).toFloat().coerceIn(startFrac + 0.05f, 1f)
            val fillWidthFraction = (endFrac - startFrac).coerceAtLeast(0.12f)

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = fillWidthFraction)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF38BDF8),
                                Color(0xFFFBBF24)
                            )
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Max Temp
        Text(
            text = maxFormatted,
            style = MaterialTheme.typography.bodyMedium,
            color = glassColors.textPrimary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(36.dp)
        )
    }
}
