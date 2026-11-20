package com.glasssky.weather.ui.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glasssky.weather.data.model.CurrentWeather
import com.glasssky.weather.data.model.WeatherConditionMapper
import com.glasssky.weather.data.model.WeatherLocation
import com.glasssky.weather.ui.theme.LocalGlassColors
import kotlin.math.roundToInt

@Composable
fun HeroWeatherCard(
    location: WeatherLocation,
    current: CurrentWeather,
    lastUpdated: Long,
    isFromCache: Boolean,
    isCelsius: Boolean,
    isMetric: Boolean = true,
    daylightHours: Int = 8,
    precipitationChance: Int = 0,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current
    val condition = WeatherConditionMapper.getCondition(current.weatherCode, current.isDay)

    val targetTempInt = if (isCelsius) {
        current.temperature.roundToInt()
    } else {
        ((current.temperature * 9 / 5) + 32).roundToInt()
    }

    // Smooth temperature value transition
    val animatedTemp by animateIntAsState(
        targetValue = targetTempInt,
        animationSpec = tween(durationMillis = 600),
        label = "HeroTempAnimation"
    )

    // Formatted wind speed
    val windSpeedText = if (isMetric) {
        "${current.windSpeed.roundToInt()}km/hr"
    } else {
        "${(current.windSpeed * 0.621371).roundToInt()}mph"
    }

    // Formatted humidity (padded with 0 if single digit like reference '02%')
    val humidityText = if (current.relativeHumidity < 10) {
        "0${current.relativeHumidity}%"
    } else {
        "${current.relativeHumidity}%"
    }

    // Dynamic weather condition advice sentence matching reference: "Expect high rain today."
    val conditionSentence = when {
        current.weatherCode in 51..67 || current.weatherCode in 80..82 -> "Expect high rain today."
        current.weatherCode in 95..99 -> "Thunderstorms expected today."
        current.weatherCode in 71..77 || current.weatherCode in 85..86 -> "Snowfall expected today."
        current.weatherCode in 1..3 -> if (current.isDay) "Partly cloudy today." else "Partly cloudy tonight."
        current.weatherCode == 0 -> if (current.isDay) "Clear sunny skies today." else "Clear starry skies tonight."
        current.weatherCode in 45..48 -> "Foggy conditions today."
        else -> "${condition.description} today."
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Centered 3D Weather Illustration (Masterpiece visual matching reference image)
        WeatherIllustration3D(
            atmosphere = condition.atmosphere,
            isDay = current.isDay,
            size = 196.dp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Large Temperature Typography: "29° C"
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$animatedTemp",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 58.sp,
                    lineHeight = 60.sp
                ),
                fontWeight = FontWeight.Bold,
                color = glassColors.textPrimary
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = if (isCelsius) "° c" else "° f",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp
                ),
                fontWeight = FontWeight.Normal,
                color = glassColors.textPrimary.copy(alpha = 0.90f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 3. Descriptive Condition Sentence: "Expect high rain today."
        Text(
            text = conditionSentence,
            style = MaterialTheme.typography.bodyLarge,
            color = glassColors.textSecondary,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Clean Horizontal Telemetry Bar: "༄ 11km/hr       💧 02%       ☀️ 8hr"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Wind
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Air,
                    contentDescription = "Wind speed",
                    tint = glassColors.textSecondary,
                    modifier = Modifier.size(19.dp)
                )
                Text(
                    text = windSpeedText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = glassColors.textPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }

            // Humidity
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = "Humidity",
                    tint = glassColors.textSecondary,
                    modifier = Modifier.size(19.dp)
                )
                Text(
                    text = humidityText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = glassColors.textPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }

            // Daylight / Sun Hours
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = "Daylight",
                    tint = glassColors.textSecondary,
                    modifier = Modifier.size(19.dp)
                )
                Text(
                    text = "${daylightHours}hr",
                    style = MaterialTheme.typography.bodyMedium,
                    color = glassColors.textPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Secondary Ambient Info Pill (Feels like, Rain %, Sunrise & Sunset)
        val feelsLikeFormatted = if (isCelsius) {
            "${current.apparentTemperature.roundToInt()}°"
        } else {
            "${((current.apparentTemperature * 9 / 5) + 32).roundToInt()}°"
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Feels like $feelsLikeFormatted",
                style = MaterialTheme.typography.bodySmall,
                color = glassColors.textSecondary,
                fontSize = 13.sp
            )
            Box(modifier = Modifier.size(3.dp).clip(androidx.compose.foundation.shape.CircleShape).background(glassColors.textMuted))
            Text(
                text = "Rain $precipitationChance%",
                style = MaterialTheme.typography.bodySmall,
                color = if (precipitationChance > 30) Color(0xFF38BDF8) else glassColors.textSecondary,
                fontSize = 13.sp,
                fontWeight = if (precipitationChance > 30) FontWeight.SemiBold else FontWeight.Normal
            )
            Box(modifier = Modifier.size(3.dp).clip(androidx.compose.foundation.shape.CircleShape).background(glassColors.textMuted))
            Text(
                text = "Sunrise ${current.sunrise}",
                style = MaterialTheme.typography.bodySmall,
                color = glassColors.textSecondary,
                fontSize = 13.sp
            )
        }
    }
}
