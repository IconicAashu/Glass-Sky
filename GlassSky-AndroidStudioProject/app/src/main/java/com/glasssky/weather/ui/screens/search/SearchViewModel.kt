package com.glasssky.weather.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glasssky.weather.data.model.WeatherLocation
import com.glasssky.weather.data.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val isSearching: Boolean = false,
    val results: List<WeatherLocation> = emptyList(),
    val favorites: List<WeatherLocation> = emptyList(),
    val recentSearches: List<WeatherLocation> = emptyList(),
    val error: String? = null
)

class SearchViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadFavorites()
        loadRecentSearches()
    }

    fun loadFavorites() {
        val favs = repository.getFavoriteCities()
        _uiState.value = _uiState.value.copy(favorites = favs)
    }

    fun loadRecentSearches() {
        val recents = repository.getRecentSearches()
        _uiState.value = _uiState.value.copy(recentSearches = recents)
    }

    fun onLocationSelected(location: WeatherLocation) {
        repository.saveRecentSearch(location)
        loadRecentSearches()
    }

    fun clearRecentSearches() {
        repository.clearRecentSearches()
        _uiState.value = _uiState.value.copy(recentSearches = emptyList())
    }

    fun onQueryChange(newQuery: String) {
        _uiState.value = _uiState.value.copy(query = newQuery, error = null)
        searchJob?.cancel()

        if (newQuery.trim().length < 2) {
            _uiState.value = _uiState.value.copy(results = emptyList(), isSearching = false)
            return
        }

        searchJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true)
            delay(350) // Debounce network requests
            try {
                val results = repository.searchCities(newQuery)
                _uiState.value = _uiState.value.copy(
                    results = results,
                    isSearching = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    error = e.localizedMessage ?: "Failed to find cities"
                )
            }
        }
    }

    fun toggleFavorite(location: WeatherLocation) {
        val currentFavs = _uiState.value.favorites.toMutableList()
        val exists = currentFavs.any { it.latitude == location.latitude && it.longitude == location.longitude }
        if (exists) {
            currentFavs.removeAll { it.latitude == location.latitude && it.longitude == location.longitude }
        } else {
            currentFavs.add(location)
        }
        repository.saveFavoriteCities(currentFavs)
        _uiState.value = _uiState.value.copy(favorites = currentFavs)
    }

    fun isFavorite(location: WeatherLocation): Boolean {
        return _uiState.value.favorites.any { it.latitude == location.latitude && it.longitude == location.longitude }
    }
}
