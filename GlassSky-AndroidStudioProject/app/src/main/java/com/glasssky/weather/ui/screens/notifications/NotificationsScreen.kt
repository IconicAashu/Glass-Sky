package com.glasssky.weather.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glasssky.weather.ui.components.GlassCard
import com.glasssky.weather.ui.theme.LocalGlassColors

@Composable
fun NotificationsScreen(
    notificationsEnabled: Boolean,
    onToggleNotifications: (Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current

    var rainAlerts by remember { mutableStateOf(true) }
    var morningBriefing by remember { mutableStateOf(true) }
    var severeWarnings by remember { mutableStateOf(true) }
    var highUvAlert by remember { mutableStateOf(false) }

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

                Text(
                    text = "Weather Alerts",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Box(modifier = Modifier.size(42.dp))
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 48.dp)
            ) {
                // Active Alert Card (if any severe condition)
                item {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0x33F97316),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFF97316).copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF97316).copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WarningAmber,
                                    contentDescription = null,
                                    tint = Color(0xFFFB923C),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Precipitation Advisory",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Expect high rainfall and damp conditions today. Carry an umbrella when stepping outdoors.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.80f),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                // Master Toggle Card
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "Push Notifications",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Enable meteorological updates & alerts",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.65f)
                                    )
                                }
                            }

                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = onToggleNotifications,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF38BDF8)
                                )
                            )
                        }
                    }
                }

                // Granular notification settings
                item {
                    Text(
                        text = "ALERT PREFERENCES",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.50f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 6.dp, top = 8.dp)
                    )
                }

                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            NotificationToggleRow(
                                title = "Rain & Downpour Alerts",
                                subtitle = "Receive alerts 15 minutes before precipitation begins",
                                icon = Icons.Default.WaterDrop,
                                isChecked = rainAlerts,
                                onCheckedChange = { rainAlerts = it },
                                enabled = notificationsEnabled
                            )

                            HorizontalDivider(
                                color = Color.White.copy(alpha = 0.08f),
                                modifier = Modifier.padding(vertical = 12.dp)
                            )

                            NotificationToggleRow(
                                title = "Morning Weather Briefing",
                                subtitle = "Daily summary delivered at 7:30 AM",
                                icon = Icons.Default.WbSunny,
                                isChecked = morningBriefing,
                                onCheckedChange = { morningBriefing = it },
                                enabled = notificationsEnabled
                            )

                            HorizontalDivider(
                                color = Color.White.copy(alpha = 0.08f),
                                modifier = Modifier.padding(vertical = 12.dp)
                            )

                            NotificationToggleRow(
                                title = "Severe Weather Warnings",
                                subtitle = "Storms, high winds, and temperature extremes",
                                icon = Icons.Default.Thunderstorm,
                                isChecked = severeWarnings,
                                onCheckedChange = { severeWarnings = it },
                                enabled = notificationsEnabled
                            )

                            HorizontalDivider(
                                color = Color.White.copy(alpha = 0.08f),
                                modifier = Modifier.padding(vertical = 12.dp)
                            )

                            NotificationToggleRow(
                                title = "High UV Index Warning",
                                subtitle = "Alert when UV reaches very high levels (> 8)",
                                icon = Icons.Default.BrightnessHigh,
                                isChecked = highUvAlert,
                                onCheckedChange = { highUvAlert = it },
                                enabled = notificationsEnabled
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) Color.White.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.35f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (enabled) Color.White else Color.White.copy(alpha = 0.40f)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (enabled) Color.White.copy(alpha = 0.60f) else Color.White.copy(alpha = 0.30f)
                )
            }
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF38BDF8)
            )
        )
    }
}
