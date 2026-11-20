package com.glasssky.weather.data.network

import com.glasssky.weather.data.model.GeocodingSearchResponse
import com.glasssky.weather.data.model.OpenMeteoAirQualityResponse
import com.glasssky.weather.data.model.OpenMeteoForecastResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class OpenMeteoService {
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = false
            })
        }
    }

    suspend fun getForecast(
        latitude: Double,
        longitude: Double,
        timezone: String = "auto"
    ): OpenMeteoForecastResponse {
        return client.get("https://api.open-meteo.com/v1/forecast") {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            parameter("timezone", timezone)
            parameter(
                "current",
                "temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,weather_code,wind_speed_10m,wind_direction_10m"
            )
            parameter(
                "hourly",
                "temperature_2m,relative_humidity_2m,apparent_temperature,precipitation_probability,weather_code,wind_speed_10m,wind_direction_10m,uv_index"
            )
            parameter(
                "daily",
                "weather_code,temperature_2m_max,temperature_2m_min,apparent_temperature_max,apparent_temperature_min,sunrise,sunset,uv_index_max,precipitation_probability_max,wind_speed_10m_max"
            )
        }.body()
    }

    suspend fun getAirQuality(
        latitude: Double,
        longitude: Double
    ): OpenMeteoAirQualityResponse {
        return client.get("https://air-quality-api.open-meteo.com/v1/air-quality") {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            parameter(
                "current",
                "us_aqi,european_aqi,pm10,pm2_5,nitrogen_dioxide,ozone"
            )
        }.body()
    }

    suspend fun searchCities(query: String): GeocodingSearchResponse {
        return client.get("https://geocoding-api.open-meteo.com/v1/search") {
            parameter("name", query.trim())
            parameter("count", 10)
            parameter("language", "en")
            parameter("format", "json")
        }.body()
    }
}
