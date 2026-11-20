package com.glasssky.weather

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.glasssky.weather.data.location.LocationTracker
import com.glasssky.weather.data.model.WeatherLocation
import com.glasssky.weather.ui.screens.favorites.FavoritesSheet
import com.glasssky.weather.ui.screens.forecast.SevenDayForecastScreen
import com.glasssky.weather.ui.screens.home.HomeScreen
import com.glasssky.weather.ui.screens.home.HomeUiState
import com.glasssky.weather.ui.screens.home.HomeViewModel
import com.glasssky.weather.ui.screens.map.WeatherMapScreen
import com.glasssky.weather.ui.screens.notifications.NotificationsScreen
import com.glasssky.weather.ui.screens.search.SearchScreen
import com.glasssky.weather.ui.screens.search.SearchViewModel
import com.glasssky.weather.ui.screens.settings.SettingsScreen
import com.glasssky.weather.ui.screens.splash.SplashScreen
import com.glasssky.weather.ui.theme.GlassSkyTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as GlassSkyApplication
        val repository = app.weatherRepository
        val locationTracker = LocationTracker(applicationContext)

        setContent {
            val themeMode by repository.themeMode.collectAsState()
            val isCelsius by repository.isCelsius.collectAsState()
            val windUnit by repository.windUnit.collectAsState()
            val notificationsEnabled by repository.notificationsEnabled.collectAsState()
            val weatherAnimationsEnabled by repository.weatherAnimationsEnabled.collectAsState()
            val animationIntensity by repository.animationIntensity.collectAsState()
            val dynamicBackgroundEnabled by repository.dynamicBackgroundEnabled.collectAsState()
            val rainNotifications by repository.rainNotifications.collectAsState()
            val dailyBriefing by repository.dailyBriefing.collectAsState()
            val refreshInterval by repository.refreshInterval.collectAsState()
            val language by repository.language.collectAsState()

            GlassSkyTheme(themeMode = themeMode) {
                val navController = rememberNavController()

                val homeViewModel: HomeViewModel = viewModel {
                    HomeViewModel(repository, locationTracker)
                }
                val searchViewModel: SearchViewModel = viewModel {
                    SearchViewModel(repository)
                }

                val homeUiState by homeViewModel.uiState.collectAsState()
                val searchUiState by searchViewModel.uiState.collectAsState()

                // Location permissions request
                val locationPermissionsState = rememberMultiplePermissionsState(
                    permissions = listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )

                LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
                    if (locationPermissionsState.allPermissionsGranted) {
                        homeViewModel.initializeWeather()
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = "splash",
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 1. Premium Animated Splash Screen
                    composable("splash") {
                        SplashScreen(
                            onSplashFinished = {
                                navController.navigate("home") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        )
                    }

                    // 2. Main Atmospheric Weather Home Screen
                    composable("home") {
                        HomeScreen(
                            uiState = homeUiState,
                            onRefresh = { homeViewModel.refresh() },
                            onOpenSearch = {
                                searchViewModel.loadFavorites()
                                searchViewModel.loadRecentSearches()
                                navController.navigate("search")
                            },
                            onOpenNotifications = {
                                navController.navigate("notifications")
                            },
                            onOpenMap = {
                                navController.navigate("map")
                            },
                            onOpenCalendarForecast = {
                                navController.navigate("forecast")
                            },
                            onOpenFavorites = {
                                searchViewModel.loadFavorites()
                                navController.navigate("favorites")
                            },
                            onOpenSettings = {
                                navController.navigate("settings")
                            },
                            onToggleUnits = { homeViewModel.toggleUnits() },
                            onRetry = { homeViewModel.refresh() }
                        )
                    }

                    // 3. Live Weather Radar / Map Screen
                    composable("map") {
                        val weatherData = (homeUiState as? HomeUiState.Success)?.data
                        val location = weatherData?.location ?: WeatherLocation("mumbai_default", "Mumbai", "India", null, 19.0760, 72.8777)
                        val currentTemp = weatherData?.current?.temperature ?: 29.0

                        WeatherMapScreen(
                            location = location,
                            currentTemp = currentTemp,
                            isCelsius = isCelsius,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 4. Weather Notifications & Alerts Screen
                    composable("notifications") {
                        NotificationsScreen(
                            notificationsEnabled = notificationsEnabled,
                            onToggleNotifications = { repository.setNotificationsEnabled(it) },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 5. 7-Day Extended Forecast Screen
                    composable("forecast") {
                        val weatherData = (homeUiState as? HomeUiState.Success)?.data
                        val location = weatherData?.location ?: WeatherLocation("mumbai_default", "Mumbai", "India", null, 19.0760, 72.8777)
                        val dailyList = weatherData?.daily ?: emptyList()

                        SevenDayForecastScreen(
                            location = location,
                            dailyList = dailyList,
                            isCelsius = isCelsius,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 6. Dedicated Settings Screen
                    composable("settings") {
                        SettingsScreen(
                            currentTheme = themeMode,
                            isCelsius = isCelsius,
                            windUnit = windUnit,
                            notificationsEnabled = notificationsEnabled,
                            weatherAnimationsEnabled = weatherAnimationsEnabled,
                            animationIntensity = animationIntensity,
                            dynamicBackgroundEnabled = dynamicBackgroundEnabled,
                            rainNotifications = rainNotifications,
                            dailyBriefing = dailyBriefing,
                            refreshInterval = refreshInterval,
                            language = language,
                            savedCitiesCount = searchUiState.favorites.size,
                            onSelectTheme = { repository.setThemeMode(it) },
                            onToggleCelsius = { repository.toggleCelsius() },
                            onSelectWindUnit = { repository.setWindUnit(it) },
                            onToggleNotifications = { repository.setNotificationsEnabled(it) },
                            onToggleAnimations = { repository.setWeatherAnimationsEnabled(it) },
                            onSelectIntensity = { repository.setAnimationIntensity(it) },
                            onToggleDynamicBg = { repository.setDynamicBackgroundEnabled(it) },
                            onToggleRainNotifs = { repository.setRainNotifications(it) },
                            onToggleDailyBriefing = { repository.setDailyBriefing(it) },
                            onSelectRefreshInterval = { repository.setRefreshInterval(it) },
                            onSelectLanguage = { repository.setLanguage(it) },
                            onOpenSavedCities = {
                                searchViewModel.loadFavorites()
                                navController.navigate("favorites")
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 7. Worldwide City Search with Autocomplete & Recent Searches
                    composable("search") {
                        SearchScreen(
                            uiState = searchUiState,
                            onQueryChange = { searchViewModel.onQueryChange(it) },
                            onLocationSelect = { loc ->
                                searchViewModel.onLocationSelected(loc)
                                homeViewModel.selectLocation(loc)
                                navController.popBackStack()
                            },
                            onToggleFavorite = { searchViewModel.toggleFavorite(it) },
                            onClearRecents = { searchViewModel.clearRecentSearches() },
                            isFavorite = { searchViewModel.isFavorite(it) },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 8. Favorite Locations Sheet
                    composable("favorites") {
                        FavoritesSheet(
                            favorites = searchUiState.favorites,
                            onSelectLocation = { loc ->
                                homeViewModel.selectLocation(loc)
                                navController.popBackStack()
                            },
                            onDeleteFavorite = { searchViewModel.toggleFavorite(it) },
                            onClose = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
