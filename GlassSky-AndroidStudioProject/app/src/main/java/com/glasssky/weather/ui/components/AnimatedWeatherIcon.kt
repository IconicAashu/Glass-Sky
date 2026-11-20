package com.glasssky.weather.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.glasssky.weather.data.model.AtmosphereType
import com.glasssky.weather.data.model.WeatherConditionMapper

@Composable
fun AnimatedWeatherIcon(
    weatherCode: Int,
    isDay: Boolean,
    modifier: Modifier = Modifier,
    iconSize: Dp = 64.dp
) {
    val condition = WeatherConditionMapper.getCondition(weatherCode, isDay)
    val infiniteTransition = rememberInfiniteTransition(label = "WeatherIconAnim")

    // Sun gentle continuous rotation
    val sunRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SunRotation"
    )

    // Breathing pulse for cloud/moon/rain
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    Box(
        modifier = modifier.size(iconSize),
        contentAlignment = Alignment.Center
    ) {
        when (condition.atmosphere) {
            AtmosphereType.CLEAR_DAY -> {
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = condition.description,
                    tint = Color(0xFFFBBF24),
                    modifier = Modifier
                        .size(iconSize)
                        .rotate(sunRotation)
                        .scale(pulseScale)
                )
            }
            AtmosphereType.CLEAR_NIGHT -> {
                Icon(
                    imageVector = Icons.Default.Bedtime,
                    contentDescription = condition.description,
                    tint = Color(0xFF93C5FD),
                    modifier = Modifier
                        .size(iconSize)
                        .scale(pulseScale)
                )
            }
            AtmosphereType.PARTLY_CLOUDY_DAY -> {
                Icon(
                    imageVector = Icons.Default.CloudQueue,
                    contentDescription = condition.description,
                    tint = Color.White,
                    modifier = Modifier
                        .size(iconSize)
                        .scale(pulseScale)
                )
            }
            AtmosphereType.PARTLY_CLOUDY_NIGHT -> {
                Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = condition.description,
                    tint = Color(0xFFCBD5E1),
                    modifier = Modifier
                        .size(iconSize)
                        .scale(pulseScale)
                )
            }
            AtmosphereType.OVERCAST -> {
                Icon(
                    imageVector = Icons.Default.FilterDrama,
                    contentDescription = condition.description,
                    tint = Color(0xFFE2E8F0),
                    modifier = Modifier
                        .size(iconSize)
                        .scale(pulseScale)
                )
            }
            AtmosphereType.RAINY -> {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = condition.description,
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier
                        .size(iconSize)
                        .scale(pulseScale)
                )
            }
            AtmosphereType.THUNDERSTORM -> {
                Icon(
                    imageVector = Icons.Default.Thunderstorm,
                    contentDescription = condition.description,
                    tint = Color(0xFFC084FC),
                    modifier = Modifier
                        .size(iconSize)
                        .scale(pulseScale)
                )
            }
            AtmosphereType.SNOWY -> {
                Icon(
                    imageVector = Icons.Default.AcUnit,
                    contentDescription = condition.description,
                    tint = Color(0xFFBAE6FD),
                    modifier = Modifier
                        .size(iconSize)
                        .rotate(sunRotation * 0.5f)
                )
            }
            AtmosphereType.FOGGY -> {
                Icon(
                    imageVector = Icons.Default.BlurOn,
                    contentDescription = condition.description,
                    tint = Color(0xFFD4D4D8),
                    modifier = Modifier
                        .size(iconSize)
                        .scale(pulseScale)
                )
            }
        }
    }
}
