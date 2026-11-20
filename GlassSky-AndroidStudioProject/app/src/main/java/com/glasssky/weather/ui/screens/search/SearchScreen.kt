package com.glasssky.weather.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glasssky.weather.data.model.AtmosphereType
import com.glasssky.weather.data.model.WeatherLocation
import com.glasssky.weather.ui.components.AtmosphericBackground
import com.glasssky.weather.ui.components.GlassCard
import com.glasssky.weather.ui.theme.LocalGlassColors

@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onQueryChange: (String) -> Unit,
    onLocationSelect: (WeatherLocation) -> Unit,
    onToggleFavorite: (WeatherLocation) -> Unit,
    onClearRecents: () -> Unit,
    isFavorite: (WeatherLocation) -> Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current

    val popularCities = listOf(
        WeatherLocation("mumbai", "Mumbai", "India", "Maharashtra", 19.0760, 72.8777, "Asia/Kolkata"),
        WeatherLocation("tokyo", "Tokyo", "Japan", "Tokyo", 35.6895, 139.6917, "Asia/Tokyo"),
        WeatherLocation("london", "London", "United Kingdom", "England", 51.5085, -0.1257, "Europe/London"),
        WeatherLocation("newyork", "New York", "United States", "New York", 40.7128, -74.006, "America/New_York"),
        WeatherLocation("paris", "Paris", "France", "Île-de-France", 48.8534, 2.3488, "Europe/Paris"),
        WeatherLocation("dubai", "Dubai", "United Arab Emirates", "Dubai", 25.2048, 55.2708, "Asia/Dubai"),
        WeatherLocation("sydney", "Sydney", "Australia", "New South Wales", -33.8688, 151.2093, "Australia/Sydney")
    )

    AtmosphericBackground(atmosphere = AtmosphereType.PARTLY_CLOUDY_NIGHT, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            // Search Bar Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = glassColors.iconTint
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = "Search cities worldwide...",
                            color = glassColors.textMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = glassColors.textSecondary
                        )
                    },
                    trailingIcon = {
                        if (uiState.query.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = glassColors.textSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = glassColors.textPrimary,
                        unfocusedTextColor = glassColors.textPrimary,
                        focusedContainerColor = if (glassColors.isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.85f),
                        unfocusedContainerColor = if (glassColors.isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.65f),
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = glassColors.cardBorderTop,
                        cursorColor = Color(0xFF38BDF8)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Loading indicator
            if (uiState.isSearching) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF38BDF8),
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Results or Suggestions List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (uiState.query.isEmpty()) {
                    // 1. RECENT SEARCHES SECTION (if any)
                    if (uiState.recentSearches.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        tint = glassColors.textMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "RECENT SEARCHES",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = glassColors.textMuted,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }

                                Text(
                                    text = "Clear",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.clickable(onClick = onClearRecents)
                                )
                            }
                        }

                        items(uiState.recentSearches, key = { "recent_${it.id}" }) { loc ->
                            CityResultCard(
                                location = loc,
                                isFav = isFavorite(loc),
                                onSelect = { onLocationSelect(loc) },
                                onToggleFav = { onToggleFavorite(loc) }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    // 2. POPULAR CITIES WORLDWIDE
                    item {
                        Text(
                            text = "POPULAR CITIES",
                            style = MaterialTheme.typography.labelSmall,
                            color = glassColors.textMuted,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                        )
                    }

                    items(popularCities, key = { "popular_${it.id}" }) { loc ->
                        CityResultCard(
                            location = loc,
                            isFav = isFavorite(loc),
                            onSelect = { onLocationSelect(loc) },
                            onToggleFav = { onToggleFavorite(loc) }
                        )
                    }
                } else if (uiState.results.isEmpty() && uiState.query.length >= 2 && !uiState.isSearching) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No matching cities found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = glassColors.textMuted
                            )
                        }
                    }
                } else {
                    items(
                        items = uiState.results,
                        key = { it.id }
                    ) { location ->
                        CityResultCard(
                            location = location,
                            isFav = isFavorite(location),
                            onSelect = { onLocationSelect(location) },
                            onToggleFav = { onToggleFavorite(location) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CityResultCard(
    location: WeatherLocation,
    isFav: Boolean,
    onSelect: () -> Unit,
    onToggleFav: () -> Unit
) {
    val glassColors = LocalGlassColors.current

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        elevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = glassColors.textPrimary
                )

                val details = listOfNotNull(location.admin1, location.country).filter { it.isNotEmpty() }
                if (details.isNotEmpty()) {
                    Text(
                        text = details.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = glassColors.textSecondary
                    )
                }
            }

            IconButton(onClick = onToggleFav) {
                Icon(
                    imageVector = if (isFav) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = if (isFav) "Remove Favorite" else "Save Favorite",
                    tint = if (isFav) Color(0xFFFBBF24) else glassColors.textMuted
                )
            }
        }
    }
}
