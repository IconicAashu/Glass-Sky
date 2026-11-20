package com.glasssky.weather.ui.screens.home

import com.glasssky.weather.data.model.WeatherDomainData

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val data: WeatherDomainData,
        val isRefreshing: Boolean = false,
        val isCelsius: Boolean = true,
        val isMetric: Boolean = true
    ) : HomeUiState
    data class Error(
        val message: String,
        val cachedData: WeatherDomainData? = null
    ) : HomeUiState
}
