package com.glasssky.weather.ui.screens.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.glasssky.weather.data.model.AtmosphereType
import com.glasssky.weather.data.model.WeatherLocation
import com.glasssky.weather.ui.components.AtmosphericBackground
import com.glasssky.weather.ui.components.GlassCard
import com.glasssky.weather.ui.theme.LocalGlassColors

@Composable
fun FavoritesSheet(
    favorites: List<WeatherLocation>,
    onSelectLocation: (WeatherLocation) -> Unit,
    onDeleteFavorite: (WeatherLocation) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current

    AtmosphericBackground(atmosphere = AtmosphereType.PARTLY_CLOUDY_NIGHT, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationCity,
                        contentDescription = null,
                        tint = glassColors.iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Saved Cities",
                        style = MaterialTheme.typography.titleLarge,
                        color = glassColors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = glassColors.iconTint
                    )
                }
            }

            if (favorites.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No saved locations yet.\nTap the bookmark icon on any city to add it to your favorites.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = glassColors.textMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = favorites,
                        key = { it.id }
                    ) { location ->
                        FavoriteCityCard(
                            location = location,
                            onSelect = { onSelectLocation(location) },
                            onDelete = { onDeleteFavorite(location) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteCityCard(
    location: WeatherLocation,
    onSelect: () -> Unit,
    onDelete: () -> Unit
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

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = Color(0xFFF87171).copy(alpha = 0.85f)
                )
            }
        }
    }
}
