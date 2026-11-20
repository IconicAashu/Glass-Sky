package com.glasssky.weather

import android.app.Application
import com.glasssky.weather.data.network.OpenMeteoService
import com.glasssky.weather.data.repository.WeatherRepository

class GlassSkyApplication : Application() {
    lateinit var weatherRepository: WeatherRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val apiService = OpenMeteoService()
        weatherRepository = WeatherRepository(applicationContext, apiService)
    }
}
