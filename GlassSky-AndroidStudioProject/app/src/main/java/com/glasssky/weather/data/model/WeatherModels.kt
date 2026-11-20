package com.glasssky.weather.data.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenMeteoForecastResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val current: CurrentUnitsDto? = null,
    val hourly: HourlyUnitsDto? = null,
    val daily: DailyUnitsDto? = null
)

@Serializable
data class CurrentUnitsDto(
    val time: String,
    @SerialName("temperature_2m") val temperature: Double,
    @SerialName("relative_humidity_2m") val relativeHumidity: Int,
    @SerialName("apparent_temperature") val apparentTemperature: Double,
    @SerialName("is_day") val isDay: Int,
    val precipitation: Double,
    @SerialName("weather_code") val weatherCode: Int,
    @SerialName("wind_speed_10m") val windSpeed: Double,
    @SerialName("wind_direction_10m") val windDirection: Int
)

@Serializable
data class HourlyUnitsDto(
    val time: List<String>,
    @SerialName("temperature_2m") val temperature: List<Double>,
    @SerialName("relative_humidity_2m") val relativeHumidity: List<Int>,
    @SerialName("apparent_temperature") val apparentTemperature: List<Double>,
    @SerialName("precipitation_probability") val precipitationProbability: List<Int?>,
    @SerialName("weather_code") val weatherCode: List<Int>,
    @SerialName("wind_speed_10m") val windSpeed: List<Double>,
    @SerialName("wind_direction_10m") val windDirection: List<Int>,
    @SerialName("uv_index") val uvIndex: List<Double?>
)

@Serializable
data class DailyUnitsDto(
    val time: List<String>,
    @SerialName("weather_code") val weatherCode: List<Int>,
    @SerialName("temperature_2m_max") val temperatureMax: List<Double>,
    @SerialName("temperature_2m_min") val temperatureMin: List<Double>,
    @SerialName("apparent_temperature_max") val apparentTemperatureMax: List<Double>,
    @SerialName("apparent_temperature_min") val apparentTemperatureMin: List<Double>,
    val sunrise: List<String>,
    val sunset: List<String>,
    @SerialName("uv_index_max") val uvIndexMax: List<Double?>,
    @SerialName("precipitation_probability_max") val precipitationProbabilityMax: List<Int?>,
    @SerialName("wind_speed_10m_max") val windSpeedMax: List<Double?>
)

@Serializable
data class GeocodingSearchResponse(
    val results: List<GeocodingResultDto>? = null
)

@Serializable
data class GeocodingResultDto(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    @SerialName("country_code") val countryCode: String? = null,
    val admin1: String? = null,
    val timezone: String? = null
)

// Immutable Domain Models for high-performance Compose recomposition skipping
@Immutable
@Serializable
data class WeatherLocation(
    val id: String,
    val name: String,
    val country: String,
    val admin1: String? = null,
    val latitude: Double,
    val longitude: Double,
    val timezone: String? = null
)

@Immutable
data class CurrentWeather(
    val temperature: Double,
    val apparentTemperature: Double,
    val relativeHumidity: Int,
    val weatherCode: Int,
    val isDay: Boolean,
    val windSpeed: Double,
    val windDirection: Int,
    val precipitation: Double,
    val uvIndex: Double,
    val sunrise: String,
    val sunset: String,
    val tempMax: Double,
    val tempMin: Double,
    val timestamp: String
)

@Immutable
data class HourlyItem(
    val time: String,
    val formattedTime: String,
    val temperature: Double,
    val apparentTemperature: Double,
    val weatherCode: Int,
    val precipitationProbability: Int,
    val humidity: Int,
    val windSpeed: Double,
    val isDay: Boolean
)

@Immutable
data class DailyItem(
    val date: String,
    val dayName: String,
    val weatherCode: Int,
    val tempMax: Double,
    val tempMin: Double,
    val precipitationProbability: Int,
    val sunrise: String,
    val sunset: String,
    val uvIndexMax: Double,
    val windSpeedMax: Double
)

@Serializable
data class OpenMeteoAirQualityResponse(
    val latitude: Double,
    val longitude: Double,
    val current: AirQualityCurrentDto? = null
)

@Serializable
data class AirQualityCurrentDto(
    val time: String? = null,
    @SerialName("us_aqi") val usAqi: Int? = null,
    @SerialName("european_aqi") val europeanAqi: Int? = null,
    @SerialName("pm10") val pm10: Double? = null,
    @SerialName("pm2_5") val pm25: Double? = null,
    @SerialName("nitrogen_dioxide") val no2: Double? = null,
    @SerialName("ozone") val ozone: Double? = null
)

@Immutable
@Serializable
data class AirQualityData(
    val aqi: Int,
    val category: String,
    val healthAdvice: String,
    val pm25: Double,
    val pm10: Double,
    val ozone: Double,
    val no2: Double
)

@Immutable
data class WeatherDomainData(
    val location: WeatherLocation,
    val current: CurrentWeather,
    val hourly: List<HourlyItem>,
    val daily: List<DailyItem>,
    val airQuality: AirQualityData? = null,
    val lastUpdated: Long,
    val isFromCache: Boolean = false
)
