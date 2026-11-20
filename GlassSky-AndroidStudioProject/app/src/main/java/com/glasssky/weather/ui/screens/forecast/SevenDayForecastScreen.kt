package com.glasssky.weather.ui.screens.forecast

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glasssky.weather.data.model.DailyItem
import com.glasssky.weather.data.model.WeatherConditionMapper
import com.glasssky.weather.data.model.WeatherLocation
import com.glasssky.weather.ui.components.GlassCard
import com.glasssky.weather.ui.components.MiniWeatherIllustration3D
import kotlin.math.roundToInt

@Composable
fun SevenDayForecastScreen(
    location: WeatherLocation,
    dailyList: List<DailyItem>,
    isCelsius: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val minTempOverall = dailyList.minOfOrNull { it.tempMin } ?: 0.0
    val maxTempOverall = dailyList.maxOfOrNull { it.tempMax } ?: 35.0
    val tempRange = (maxTempOverall - minTempOverall).coerceAtLeast(1.0)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF161B26).copy(alpha = 0.85f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "7-Day Forecast",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${location.name}, ${location.country}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.70f)
                    )
                }

                Box(modifier = Modifier.size(42.dp))
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 48.dp)
            ) {
                // Overview Banner
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Weekly Atmospheric Outlook",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Expect temperature ranges between ${minTempOverall.toInt()}° and ${maxTempOverall.toInt()}° over the next 7 days.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.75f),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                // Daily Cards
                items(dailyList) { item ->
                    val condition = WeatherConditionMapper.getCondition(item.weatherCode, isDay = true)
                    val minFormatted = if (isCelsius) "${item.tempMin.roundToInt()}°" else "${((item.tempMin * 9 / 5) + 32).roundToInt()}°"
                    val maxFormatted = if (isCelsius) "${item.tempMax.roundToInt()}°" else "${((item.tempMax * 9 / 5) + 32).roundToInt()}°"

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    MiniWeatherIllustration3D(
                                        atmosphere = condition.atmosphere,
                                        isDay = true,
                                        size = 36.dp
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = item.dayName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = condition.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.65f)
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (item.precipitationProbability > 0) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0xFF38BDF8).copy(alpha = 0.20f),
                                            modifier = Modifier.padding(end = 12.dp)
                                        ) {
                                            Text(
                                                text = "${item.precipitationProbability}%",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF38BDF8),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = "$minFormatted - $maxFormatted",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Temperature Range Bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color.White.copy(alpha = 0.10f))
                            ) {
                                val startFrac = ((item.tempMin - minTempOverall) / tempRange).toFloat().coerceIn(0f, 1f)
                                val endFrac = ((item.tempMax - minTempOverall) / tempRange).toFloat().coerceIn(startFrac + 0.05f, 1f)
                                val fillWidth = (endFrac - startFrac).coerceAtLeast(0.12f)

                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(fraction = fillWidth)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(Color(0xFF38BDF8), Color(0xFFFBBF24))
                                            )
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
