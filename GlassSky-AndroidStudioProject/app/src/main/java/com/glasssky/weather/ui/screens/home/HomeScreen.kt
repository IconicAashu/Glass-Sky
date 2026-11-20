package com.glasssky.weather.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glasssky.weather.data.model.AtmosphereType
import com.glasssky.weather.data.model.WeatherConditionMapper
import com.glasssky.weather.ui.components.*
import com.glasssky.weather.ui.theme.LocalGlassColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onRefresh: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenMap: () -> Unit,
    onOpenCalendarForecast: () -> Unit,
    onOpenFavorites: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleUnits: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val atmosphere = when (uiState) {
        is HomeUiState.Success -> {
            WeatherConditionMapper.getCondition(
                uiState.data.current.weatherCode,
                uiState.data.current.isDay
            ).atmosphere
        }
        else -> AtmosphereType.CLEAR_DAY
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xF7101522),
                drawerContentColor = Color.White,
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.15f),
                        RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(24.dp)
                ) {
                    // Drawer Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF38BDF8).copy(alpha = 0.20f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CloudQueue,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Glass Sky",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Premium Weather",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.65f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.12f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Menu Actions
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.BookmarkBorder, contentDescription = null, tint = Color.White) },
                        label = { Text("Saved Locations", color = Color.White) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onOpenFavorites()
                        },
                        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                        label = { Text("Search Worldwide", color = Color.White) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onOpenSearch()
                        },
                        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color.White) },
                        label = { Text("7-Day Forecast", color = Color.White) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onOpenCalendarForecast()
                        },
                        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Map, contentDescription = null, tint = Color.White) },
                        label = { Text("Weather Radar Map", color = Color.White) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onOpenMap()
                        },
                        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = Color.White) },
                        label = { Text("Weather Alerts", color = Color.White) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onOpenNotifications()
                        },
                        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Quick Units & Settings
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable(onClick = onToggleUnits)
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Temperature Unit",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                        val isCelsius = (uiState as? HomeUiState.Success)?.isCelsius ?: true
                        Text(
                            text = if (isCelsius) "°C (Metric)" else "°F (Imperial)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White) },
                        label = { Text("Settings", color = Color.White) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onOpenSettings()
                        },
                        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )
                }
            }
        }
    ) {
        AtmosphericBackground(atmosphere = atmosphere, modifier = modifier) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Top Bar matching reference: Left drawer icon "=", Center City & Country, Right Calendar icon
                    ReferenceTopBar(
                        cityName = (uiState as? HomeUiState.Success)?.data?.location?.name ?: "Mumbai",
                        countryName = (uiState as? HomeUiState.Success)?.data?.location?.country ?: "India",
                        onOpenMenu = { scope.launch { drawerState.open() } },
                        onOpenCalendar = onOpenCalendarForecast
                    )

                    // Content Body based on state
                    when (uiState) {
                        is HomeUiState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF38BDF8),
                                        strokeWidth = 3.dp,
                                        modifier = Modifier.size(42.dp)
                                    )
                                    Spacer(modifier = Modifier.height(18.dp))
                                    Text(
                                        text = "Connecting to atmosphere...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.75f)
                                    )
                                }
                            }
                        }
                        is HomeUiState.Error -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(
                                        modifier = Modifier.padding(28.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CloudOff,
                                            contentDescription = null,
                                            tint = Color(0xFFF87171),
                                            modifier = Modifier.size(52.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "Connection Offline",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = glassColors.textPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = uiState.message,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = glassColors.textSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Button(
                                            onClick = onRetry,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF38BDF8),
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(14.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Try Again")
                                        }
                                    }
                                }
                            }
                        }
                        is HomeUiState.Success -> {
                            val weather = uiState.data
                            val scrollState = rememberScrollState()
                            val rainChance = weather.daily.firstOrNull()?.precipitationProbability ?: 0

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .verticalScroll(scrollState)
                                    .padding(bottom = 96.dp) // Clearance for floating bottom nav
                            ) {
                                // 1. Hero Weather Section (3D Illustration, Big Temp, Condition, Telemetry Row)
                                HeroWeatherCard(
                                    location = weather.location,
                                    current = weather.current,
                                    lastUpdated = weather.lastUpdated,
                                    isFromCache = weather.isFromCache,
                                    isCelsius = uiState.isCelsius,
                                    isMetric = uiState.isMetric,
                                    daylightHours = 8,
                                    precipitationChance = rainChance
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Real Air Quality Index Card
                                if (weather.airQuality != null) {
                                    AirQualityCard(airQuality = weather.airQuality)
                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                // 2. Hourly Forecast Section (Clock icon + Squircle Cards)
                                HourlyForecastRow(
                                    hourlyList = weather.hourly,
                                    isCelsius = uiState.isCelsius
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // 3. 7-Day Forecast Section
                                DailyForecastList(
                                    dailyList = weather.daily,
                                    isCelsius = uiState.isCelsius
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // 4. Detailed Telemetry Metrics Grid
                                WeatherMetricsGrid(
                                    current = weather.current,
                                    precipitationChance = rainChance,
                                    isMetric = uiState.isMetric
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "Meteorological telemetry provided by Open-Meteo.com",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.45f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // Floating Glass Bottom Navigation Bar matching reference image!
                FloatingBottomNav(
                    selectedTab = BottomNavTab.HOME,
                    onTabSelected = { tab ->
                        when (tab) {
                            BottomNavTab.HOME -> { /* Already on Home */ }
                            BottomNavTab.SEARCH -> onOpenSearch()
                            BottomNavTab.NOTIFICATIONS -> onOpenNotifications()
                            BottomNavTab.MAP -> onOpenMap()
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

/**
 * Top bar precisely matching the reference image:
 * Left: Menu drawer "=" (two clean horizontal rounded bars)
 * Center: "City, Country"
 * Right: Calendar icon "📅"
 */
@Composable
private fun ReferenceTopBar(
    cityName: String,
    countryName: String,
    onOpenMenu: () -> Unit,
    onOpenCalendar: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 22.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Clean Menu Drawer Button "="
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable(onClick = onOpenMenu),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(20.dp, 14.dp)) {
                // Two clean rounded bars matching the reference image
                val barWidth = size.width
                val barHeight = 2.4.dp.toPx()
                drawLine(
                    color = Color.White,
                    start = Offset(0f, barHeight / 2f),
                    end = Offset(barWidth, barHeight / 2f),
                    strokeWidth = barHeight,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color.White,
                    start = Offset(0f, size.height - barHeight / 2f),
                    end = Offset(barWidth * 0.72f, size.height - barHeight / 2f),
                    strokeWidth = barHeight,
                    cap = StrokeCap.Round
                )
            }
        }

        // Center: City & Country ("Mumbai, India")
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val locationTitle = if (countryName.isNotEmpty()) "$cityName, $countryName" else cityName
            Text(
                text = locationTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.3.sp
                ),
                color = Color.White
            )
        }

        // Right: Calendar / 7-Day Forecast Button "📅"
        IconButton(
            onClick = onOpenCalendar,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "7-Day Forecast",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
