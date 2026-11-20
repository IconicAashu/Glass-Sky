package com.glasssky.weather.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class AtmosphereType {
    CLEAR_DAY,
    CLEAR_NIGHT,
    PARTLY_CLOUDY_DAY,
    PARTLY_CLOUDY_NIGHT,
    OVERCAST,
    FOGGY,
    RAINY,
    SNOWY,
    THUNDERSTORM
}

data class WeatherConditionInfo(
    val code: Int,
    val description: String,
    val atmosphere: AtmosphereType,
    val icon: ImageVector
)

object WeatherConditionMapper {
    fun getCondition(code: Int, isDay: Boolean = true): WeatherConditionInfo {
        return when (code) {
            0 -> WeatherConditionInfo(
                code = code,
                description = if (isDay) "Clear Sky" else "Clear Night",
                atmosphere = if (isDay) AtmosphereType.CLEAR_DAY else AtmosphereType.CLEAR_NIGHT,
                icon = if (isDay) Icons.Default.WbSunny else Icons.Default.NightsStay
            )
            1 -> WeatherConditionInfo(
                code = code,
                description = if (isDay) "Mainly Sunny" else "Mainly Clear",
                atmosphere = if (isDay) AtmosphereType.CLEAR_DAY else AtmosphereType.CLEAR_NIGHT,
                icon = if (isDay) Icons.Default.WbSunny else Icons.Default.NightsStay
            )
            2 -> WeatherConditionInfo(
                code = code,
                description = "Partly Cloudy",
                atmosphere = if (isDay) AtmosphereType.PARTLY_CLOUDY_DAY else AtmosphereType.PARTLY_CLOUDY_NIGHT,
                icon = Icons.Default.CloudQueue
            )
            3 -> WeatherConditionInfo(
                code = code,
                description = "Overcast",
                atmosphere = AtmosphereType.OVERCAST,
                icon = Icons.Default.Cloud
            )
            45, 48 -> WeatherConditionInfo(
                code = code,
                description = "Foggy",
                atmosphere = AtmosphereType.FOGGY,
                icon = Icons.Default.Grain
            )
            51, 53, 55 -> WeatherConditionInfo(
                code = code,
                description = "Drizzle",
                atmosphere = AtmosphereType.RAINY,
                icon = Icons.Default.Grain
            )
            61, 63 -> WeatherConditionInfo(
                code = code,
                description = "Rain",
                atmosphere = AtmosphereType.RAINY,
                icon = Icons.Default.WaterDrop
            )
            65 -> WeatherConditionInfo(
                code = code,
                description = "Heavy Rain",
                atmosphere = AtmosphereType.RAINY,
                icon = Icons.Default.Thunderstorm
            )
            71, 73, 75, 77, 85, 86 -> WeatherConditionInfo(
                code = code,
                description = "Snow",
                atmosphere = AtmosphereType.SNOWY,
                icon = Icons.Default.AcUnit
            )
            80, 81, 82 -> WeatherConditionInfo(
                code = code,
                description = "Rain Showers",
                atmosphere = AtmosphereType.RAINY,
                icon = Icons.Default.WaterDrop
            )
            95, 96, 99 -> WeatherConditionInfo(
                code = code,
                description = "Thunderstorm",
                atmosphere = AtmosphereType.THUNDERSTORM,
                icon = Icons.Default.Bolt
            )
            else -> WeatherConditionInfo(
                code = code,
                description = "Clear",
                atmosphere = if (isDay) AtmosphereType.CLEAR_DAY else AtmosphereType.CLEAR_NIGHT,
                icon = if (isDay) Icons.Default.WbSunny else Icons.Default.NightsStay
            )
        }
    }

    fun getWindDirection(degrees: Int): String {
        val normalized = ((degrees % 360) + 360) % 360
        val directions = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
        val index = ((normalized / 22.5) + 0.5).toInt() % 16
        return directions[index]
    }

    fun getUvLevel(uvIndex: Double): Pair<String, Color> {
        return when {
            uvIndex <= 2.9 -> "Low" to Color(0xFF10B981)
            uvIndex <= 5.9 -> "Moderate" to Color(0xFFF59E0B)
            uvIndex <= 7.9 -> "High" to Color(0xFFF97316)
            uvIndex <= 10.9 -> "Very High" to Color(0xFFEF4444)
            else -> "Extreme" to Color(0xFFA855F7)
        }
    }
}
