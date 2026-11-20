package com.glasssky.weather.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glasssky.weather.data.location.LocationTracker
import com.glasssky.weather.data.model.WeatherLocation
import com.glasssky.weather.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: WeatherRepository,
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentLocation: WeatherLocation = repository.getLastSelectedCity()

    init {
        // Observe unit changes from repository / settings
        viewModelScope.launch {
            repository.isCelsius.collect { isCelsius ->
                val state = _uiState.value
                if (state is HomeUiState.Success) {
                    _uiState.update {
                        (it as HomeUiState.Success).copy(
                            isCelsius = isCelsius,
                            isMetric = isCelsius
                        )
                    }
                }
            }
        }
        initializeWeather()
    }

    fun initializeWeather() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            // Try to acquire device GPS location if permission granted
            val gpsLocation = locationTracker.getCurrentLocation()
            if (gpsLocation != null) {
                currentLocation = WeatherLocation(
                    id = "current_gps",
                    name = "Current Location",
                    country = "",
                    latitude = gpsLocation.latitude,
                    longitude = gpsLocation.longitude
                )
            } else {
                currentLocation = repository.getLastSelectedCity()
            }

            fetchWeather(forceRefresh = false)
        }
    }

    fun refresh() {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            _uiState.update { (it as HomeUiState.Success).copy(isRefreshing = true) }
        }
        fetchWeather(forceRefresh = true)
    }

    fun selectLocation(location: WeatherLocation) {
        currentLocation = location
        repository.saveLastSelectedCity(location)
        _uiState.value = HomeUiState.Loading
        fetchWeather(forceRefresh = false)
    }

    fun toggleUnits() {
        repository.toggleCelsius()
    }

    private fun fetchWeather(forceRefresh: Boolean) {
        viewModelScope.launch {
            val result = repository.getWeatherData(currentLocation, forceRefresh)
            val isCelsius = repository.isCelsius.value
            result.fold(
                onSuccess = { data ->
                    _uiState.value = HomeUiState.Success(
                        data = data,
                        isRefreshing = false,
                        isCelsius = isCelsius,
                        isMetric = isCelsius
                    )
                },
                onFailure = { error ->
                    _uiState.value = HomeUiState.Error(
                        message = error.localizedMessage ?: "Unable to retrieve weather telemetry"
                    )
                }
            )
        }
    }
}
