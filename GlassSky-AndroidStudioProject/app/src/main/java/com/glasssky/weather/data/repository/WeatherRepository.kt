package com.glasssky.weather.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.glasssky.weather.data.model.*
import com.glasssky.weather.data.network.OpenMeteoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

class WeatherRepository(
    private val context: Context,
    private val apiService: OpenMeteoService
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("glasssky_weather_prefs", Context.MODE_PRIVATE)

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    // Reactive Theme and Settings StateFlows
    private val _themeMode = MutableStateFlow(loadThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _isCelsius = MutableStateFlow(prefs.getBoolean("pref_is_celsius", true))
    val isCelsius: StateFlow<Boolean> = _isCelsius.asStateFlow()

    private val _windUnit = MutableStateFlow(prefs.getString("pref_wind_unit", "km/h") ?: "km/h")
    val windUnit: StateFlow<String> = _windUnit.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(prefs.getBoolean("pref_notifications", true))
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _weatherAnimationsEnabled = MutableStateFlow(prefs.getBoolean("pref_animations_enabled", true))
    val weatherAnimationsEnabled: StateFlow<Boolean> = _weatherAnimationsEnabled.asStateFlow()

    private val _animationIntensity = MutableStateFlow(prefs.getString("pref_anim_intensity", "Normal") ?: "Normal")
    val animationIntensity: StateFlow<String> = _animationIntensity.asStateFlow()

    private val _dynamicBackgroundEnabled = MutableStateFlow(prefs.getBoolean("pref_dynamic_bg", true))
    val dynamicBackgroundEnabled: StateFlow<Boolean> = _dynamicBackgroundEnabled.asStateFlow()

    private val _rainNotifications = MutableStateFlow(prefs.getBoolean("pref_rain_notifs", true))
    val rainNotifications: StateFlow<Boolean> = _rainNotifications.asStateFlow()

    private val _dailyBriefing = MutableStateFlow(prefs.getBoolean("pref_daily_briefing", true))
    val dailyBriefing: StateFlow<Boolean> = _dailyBriefing.asStateFlow()

    private val _refreshInterval = MutableStateFlow(prefs.getString("pref_refresh_interval", "30m") ?: "30m")
    val refreshInterval: StateFlow<String> = _refreshInterval.asStateFlow()

    private val _language = MutableStateFlow(prefs.getString("pref_language", "English") ?: "English")
    val language: StateFlow<String> = _language.asStateFlow()

    private fun loadThemeMode(): ThemeMode {
        val name = prefs.getString("pref_theme_mode", ThemeMode.SYSTEM.name)
        return try {
            ThemeMode.valueOf(name ?: ThemeMode.SYSTEM.name)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("pref_theme_mode", mode.name).apply()
    }

    fun setCelsius(enabled: Boolean) {
        _isCelsius.value = enabled
        prefs.edit().putBoolean("pref_is_celsius", enabled).apply()
    }

    fun toggleCelsius() {
        setCelsius(!_isCelsius.value)
    }

    fun setWindUnit(unit: String) {
        _windUnit.value = unit
        prefs.edit().putString("pref_wind_unit", unit).apply()
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        prefs.edit().putBoolean("pref_notifications", enabled).apply()
    }

    fun setWeatherAnimationsEnabled(enabled: Boolean) {
        _weatherAnimationsEnabled.value = enabled
        prefs.edit().putBoolean("pref_animations_enabled", enabled).apply()
    }

    fun setAnimationIntensity(intensity: String) {
        _animationIntensity.value = intensity
        prefs.edit().putString("pref_anim_intensity", intensity).apply()
    }

    fun setDynamicBackgroundEnabled(enabled: Boolean) {
        _dynamicBackgroundEnabled.value = enabled
        prefs.edit().putBoolean("pref_dynamic_bg", enabled).apply()
    }

    fun setRainNotifications(enabled: Boolean) {
        _rainNotifications.value = enabled
        prefs.edit().putBoolean("pref_rain_notifs", enabled).apply()
    }

    fun setDailyBriefing(enabled: Boolean) {
        _dailyBriefing.value = enabled
        prefs.edit().putBoolean("pref_daily_briefing", enabled).apply()
    }

    fun setRefreshInterval(interval: String) {
        _refreshInterval.value = interval
        prefs.edit().putString("pref_refresh_interval", interval).apply()
    }

    fun setLanguage(lang: String) {
        _language.value = lang
        prefs.edit().putString("pref_language", lang).apply()
    }

    fun getRecentSearches(): List<WeatherLocation> {
        val raw = prefs.getString("recent_searches", null) ?: return emptyList()
        return try {
            json.decodeFromString<List<WeatherLocation>>(raw)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveRecentSearch(location: WeatherLocation) {
        val current = getRecentSearches().toMutableList()
        current.removeAll { it.latitude == location.latitude && it.longitude == location.longitude }
        current.add(0, location)
        val trimmed = current.take(6)
        prefs.edit().putString("recent_searches", json.encodeToString(trimmed)).apply()
    }

    fun clearRecentSearches() {
        prefs.edit().remove("recent_searches").apply()
    }

    suspend fun getWeatherData(
        location: WeatherLocation,
        forceRefresh: Boolean = false
    ): Result<WeatherDomainData> = withContext(Dispatchers.IO) {
        val cacheKey = "cache_${location.latitude}_${location.longitude}"
        val aqiCacheKey = "aqi_cache_${location.latitude}_${location.longitude}"
        val cacheTimeKey = "cache_time_${location.latitude}_${location.longitude}"

        // If not forcing refresh, check cached response
        if (!forceRefresh) {
            val cachedJson = prefs.getString(cacheKey, null)
            val cacheTime = prefs.getLong(cacheTimeKey, 0L)
            val isFresh = (System.currentTimeMillis() - cacheTime) < (15 * 60 * 1000) // 15 mins

            if (cachedJson != null && isFresh) {
                try {
                    val cachedResponse = json.decodeFromString<OpenMeteoForecastResponse>(cachedJson)
                    val cachedAqi = prefs.getString(aqiCacheKey, null)?.let {
                        try { json.decodeFromString<AirQualityData>(it) } catch (e: Exception) { null }
                    }
                    val domain = mapToDomain(location, cachedResponse, cachedAqi, cacheTime, isFromCache = true)
                    return@withContext Result.success(domain)
                } catch (e: Exception) {
                    // Fallback to network
                }
            }
        }

        try {
            coroutineScope {
                val forecastDeferred = async {
                    apiService.getForecast(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        timezone = location.timezone ?: "auto"
                    )
                }

                val airQualityDeferred = async {
                    try {
                        apiService.getAirQuality(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    } catch (e: Exception) {
                        null
                    }
                }

                val forecastResponse = forecastDeferred.await()
                val aqiResponse = airQualityDeferred.await()
                val aqiData = mapAirQuality(aqiResponse)

                val now = System.currentTimeMillis()
                val editor = prefs.edit()
                    .putString(cacheKey, json.encodeToString(forecastResponse))
                    .putLong(cacheTimeKey, now)

                if (aqiData != null) {
                    editor.putString(aqiCacheKey, json.encodeToString(aqiData))
                }
                editor.apply()

                val domain = mapToDomain(location, forecastResponse, aqiData, now, isFromCache = false)
                Result.success(domain)
            }
        } catch (e: Exception) {
            // Check if any stale cached data is available when offline
            val cachedJson = prefs.getString(cacheKey, null)
            val cacheTime = prefs.getLong(cacheTimeKey, 0L)
            if (cachedJson != null) {
                try {
                    val cachedResponse = json.decodeFromString<OpenMeteoForecastResponse>(cachedJson)
                    val cachedAqi = prefs.getString(aqiCacheKey, null)?.let {
                        try { json.decodeFromString<AirQualityData>(it) } catch (ignored: Exception) { null }
                    }
                    val domain = mapToDomain(location, cachedResponse, cachedAqi, cacheTime, isFromCache = true)
                    return@withContext Result.success(domain)
                } catch (ignored: Exception) {
                }
            }
            Result.failure(e)
        }
    }

    suspend fun searchCities(query: String): List<WeatherLocation> = withContext(Dispatchers.IO) {
        if (query.trim().length < 2) return@withContext emptyList()
        try {
            val response = apiService.searchCities(query)
            response.results?.map {
                WeatherLocation(
                    id = "${it.latitude}_${it.longitude}",
                    name = it.name,
                    country = it.country ?: "",
                    admin1 = it.admin1,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    timezone = it.timezone
                )
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getFavoriteCities(): List<WeatherLocation> {
        val raw = prefs.getString("favorite_cities", null) ?: return defaultCities()
        return try {
            json.decodeFromString<List<WeatherLocation>>(raw)
        } catch (e: Exception) {
            defaultCities()
        }
    }

    fun saveFavoriteCities(cities: List<WeatherLocation>) {
        prefs.edit().putString("favorite_cities", json.encodeToString(cities)).apply()
    }

    fun getLastSelectedCity(): WeatherLocation {
        val raw = prefs.getString("last_selected_city", null) ?: return defaultCities().first()
        return try {
            json.decodeFromString<WeatherLocation>(raw)
        } catch (e: Exception) {
            defaultCities().first()
        }
    }

    fun saveLastSelectedCity(location: WeatherLocation) {
        prefs.edit().putString("last_selected_city", json.encodeToString(location)).apply()
    }

    private fun defaultCities(): List<WeatherLocation> = listOf(
        WeatherLocation("tokyo", "Tokyo", "Japan", "Tokyo", 35.6895, 139.6917, "Asia/Tokyo"),
        WeatherLocation("london", "London", "United Kingdom", "England", 51.5085, -0.1257, "Europe/London"),
        WeatherLocation("newyork", "New York", "United States", "New York", 40.7128, -74.006, "America/New_York"),
        WeatherLocation("paris", "Paris", "France", "Île-de-France", 48.8534, 2.3488, "Europe/Paris"),
        WeatherLocation("sanfrancisco", "San Francisco", "United States", "California", 37.7749, -122.4194, "America/Los_Angeles")
    )

    private fun mapAirQuality(response: OpenMeteoAirQualityResponse?): AirQualityData? {
        val cur = response?.current ?: return null
        val aqi = cur.usAqi ?: cur.europeanAqi ?: return null
        val (category, advice) = when {
            aqi <= 50 -> "Good" to "Air quality is ideal for outdoor activities and exercise."
            aqi <= 100 -> "Moderate" to "Air quality is acceptable. Very sensitive individuals should observe symptoms."
            aqi <= 150 -> "Unhealthy for Sensitive" to "Sensitive groups should reduce prolonged outdoor exertion."
            aqi <= 200 -> "Unhealthy" to "Everyone may experience health effects. Limit prolonged outdoor exposure."
            aqi <= 300 -> "Very Unhealthy" to "Health alert: Significantly avoid strenuous outdoor activity."
            else -> "Hazardous" to "Emergency warning: Remain indoors and keep windows closed."
        }
        return AirQualityData(
            aqi = aqi,
            category = category,
            healthAdvice = advice,
            pm25 = cur.pm25 ?: 0.0,
            pm10 = cur.pm10 ?: 0.0,
            ozone = cur.ozone ?: 0.0,
            no2 = cur.no2 ?: 0.0
        )
    }

    private fun mapToDomain(
        location: WeatherLocation,
        response: OpenMeteoForecastResponse,
        airQuality: AirQualityData?,
        timestamp: Long,
        isFromCache: Boolean
    ): WeatherDomainData {
        val cur = response.current ?: throw IllegalStateException("Current weather unavailable")
        val hourly = response.hourly ?: throw IllegalStateException("Hourly weather unavailable")
        val daily = response.daily ?: throw IllegalStateException("Daily weather unavailable")

        val timeFormat = SimpleDateFormat("h a", Locale.getDefault())
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())

        // 24-hr hourly items
        val hourlyItems = mutableListOf<HourlyItem>()
        val count = minOf(hourly.time.size, 24)
        for (i in 0 until count) {
            val date = try { isoFormat.parse(hourly.time[i]) } catch (e: Exception) { null }
            val formatted = if (i == 0) "Now" else date?.let { timeFormat.format(it) } ?: hourly.time[i]
            val hour = date?.let {
                val cal = Calendar.getInstance()
                cal.time = it
                cal.get(Calendar.HOUR_OF_DAY)
            } ?: 12
            val isDay = hour in 6..19

            hourlyItems.add(
                HourlyItem(
                    time = hourly.time[i],
                    formattedTime = formatted,
                    temperature = hourly.temperature[i],
                    apparentTemperature = hourly.apparentTemperature[i],
                    weatherCode = hourly.weatherCode[i],
                    precipitationProbability = hourly.precipitationProbability.getOrNull(i) ?: 0,
                    humidity = hourly.relativeHumidity[i],
                    windSpeed = hourly.windSpeed[i],
                    isDay = isDay
                )
            )
        }

        // 7-day daily items
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dateParse = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dailyItems = mutableListOf<DailyItem>()
        val dayCount = minOf(daily.time.size, 7)

        for (i in 0 until dayCount) {
            val date = try { dateParse.parse(daily.time[i]) } catch (e: Exception) { null }
            val dayName = if (i == 0) "Today" else date?.let { dayFormat.format(it) } ?: daily.time[i]

            val sunriseStr = daily.sunrise.getOrNull(i)?.substringAfter('T') ?: "--:--"
            val sunsetStr = daily.sunset.getOrNull(i)?.substringAfter('T') ?: "--:--"

            dailyItems.add(
                DailyItem(
                    date = daily.time[i],
                    dayName = dayName,
                    weatherCode = daily.weatherCode[i],
                    tempMax = daily.temperatureMax[i],
                    tempMin = daily.temperatureMin[i],
                    precipitationProbability = daily.precipitationProbabilityMax.getOrNull(i) ?: 0,
                    sunrise = sunriseStr,
                    sunset = sunsetStr,
                    uvIndexMax = daily.uvIndexMax.getOrNull(i) ?: 0.0,
                    windSpeedMax = daily.windSpeedMax.getOrNull(i) ?: 0.0
                )
            )
        }

        val curIsDay = cur.isDay == 1
        val currentWeather = CurrentWeather(
            temperature = cur.temperature,
            apparentTemperature = cur.apparentTemperature,
            relativeHumidity = cur.relativeHumidity,
            weatherCode = cur.weatherCode,
            isDay = curIsDay,
            windSpeed = cur.windSpeed,
            windDirection = cur.windDirection,
            precipitation = cur.precipitation,
            uvIndex = daily.uvIndexMax.firstOrNull() ?: 0.0,
            sunrise = daily.sunrise.firstOrNull()?.substringAfter('T') ?: "06:00",
            sunset = daily.sunset.firstOrNull()?.substringAfter('T') ?: "18:00",
            tempMax = daily.temperatureMax.firstOrNull() ?: cur.temperature,
            tempMin = daily.temperatureMin.firstOrNull() ?: cur.temperature,
            timestamp = cur.time
        )

        return WeatherDomainData(
            location = location,
            current = currentWeather,
            hourly = hourlyItems,
            daily = dailyItems,
            airQuality = airQuality,
            lastUpdated = timestamp,
            isFromCache = isFromCache
        )
    }
}
