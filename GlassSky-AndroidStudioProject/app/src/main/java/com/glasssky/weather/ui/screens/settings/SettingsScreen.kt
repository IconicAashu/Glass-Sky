package com.glasssky.weather.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glasssky.weather.data.model.AtmosphereType
import com.glasssky.weather.data.repository.ThemeMode
import com.glasssky.weather.ui.components.AtmosphericBackground
import com.glasssky.weather.ui.components.GlassCard
import com.glasssky.weather.ui.theme.LocalGlassColors

@Composable
fun SettingsScreen(
    currentTheme: ThemeMode,
    isCelsius: Boolean,
    windUnit: String,
    notificationsEnabled: Boolean,
    weatherAnimationsEnabled: Boolean,
    animationIntensity: String,
    dynamicBackgroundEnabled: Boolean,
    rainNotifications: Boolean,
    dailyBriefing: Boolean,
    refreshInterval: String,
    language: String,
    savedCitiesCount: Int,
    onSelectTheme: (ThemeMode) -> Unit,
    onToggleCelsius: () -> Unit,
    onSelectWindUnit: (String) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onToggleAnimations: (Boolean) -> Unit,
    onSelectIntensity: (String) -> Unit,
    onToggleDynamicBg: (Boolean) -> Unit,
    onToggleRainNotifs: (Boolean) -> Unit,
    onToggleDailyBriefing: (Boolean) -> Unit,
    onSelectRefreshInterval: (String) -> Unit,
    onSelectLanguage: (String) -> Unit,
    onOpenSavedCities: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current

    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showLicensesDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showRefreshDialog by remember { mutableStateOf(false) }
    var showIntensityDialog by remember { mutableStateOf(false) }

    AtmosphericBackground(atmosphere = AtmosphereType.PARTLY_CLOUDY_NIGHT, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = glassColors.textPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        color = glassColors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Glass Sky Preferences & Personalization",
                        style = MaterialTheme.typography.bodySmall,
                        color = glassColors.textMuted
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // SECTION 1: APPEARANCE
                SettingsSection(title = "APPEARANCE") {
                    Text(
                        text = "Interface Theme",
                        style = MaterialTheme.typography.labelMedium,
                        color = glassColors.textMuted,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeOptionCard(
                            title = "System",
                            icon = Icons.Default.BrightnessAuto,
                            isSelected = currentTheme == ThemeMode.SYSTEM,
                            onClick = { onSelectTheme(ThemeMode.SYSTEM) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionCard(
                            title = "Light",
                            icon = Icons.Default.LightMode,
                            isSelected = currentTheme == ThemeMode.LIGHT,
                            onClick = { onSelectTheme(ThemeMode.LIGHT) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionCard(
                            title = "Dark",
                            icon = Icons.Default.DarkMode,
                            isSelected = currentTheme == ThemeMode.DARK,
                            onClick = { onSelectTheme(ThemeMode.DARK) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // SECTION 2: MEASUREMENT UNITS
                SettingsSection(title = "UNITS") {
                    // Temperature
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Temperature Unit",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = glassColors.textPrimary
                            )
                            Text(
                                text = if (isCelsius) "Celsius (°C) selected" else "Fahrenheit (°F) selected",
                                style = MaterialTheme.typography.bodySmall,
                                color = glassColors.textMuted
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            UnitChip(
                                label = "°C",
                                isSelected = isCelsius,
                                onClick = { if (!isCelsius) onToggleCelsius() }
                            )
                            UnitChip(
                                label = "°F",
                                isSelected = !isCelsius,
                                onClick = { if (isCelsius) onToggleCelsius() }
                            )
                        }
                    }

                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    // Wind Speed
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Wind Speed Unit",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = glassColors.textPrimary
                            )
                            Text(
                                text = "Currently $windUnit",
                                style = MaterialTheme.typography.bodySmall,
                                color = glassColors.textMuted
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            UnitChip(
                                label = "km/h",
                                isSelected = windUnit == "km/h",
                                onClick = { onSelectWindUnit("km/h") }
                            )
                            UnitChip(
                                label = "mph",
                                isSelected = windUnit == "mph",
                                onClick = { onSelectWindUnit("mph") }
                            )
                            UnitChip(
                                label = "m/s",
                                isSelected = windUnit == "m/s",
                                onClick = { onSelectWindUnit("m/s") }
                            )
                        }
                    }
                }

                // SECTION 3: PERSONALIZATION
                SettingsSection(title = "PERSONALIZATION") {
                    SettingsSwitchRow(
                        title = "Weather Animations",
                        subtitle = "Enable 3D volumetric weather illustrations & particle physics",
                        icon = Icons.Default.Animation,
                        checked = weatherAnimationsEnabled,
                        onCheckedChange = onToggleAnimations
                    )

                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    SettingsClickableRow(
                        title = "Animation Intensity",
                        subtitle = "Currently set to $animationIntensity",
                        icon = Icons.Default.Speed,
                        onClick = { showIntensityDialog = true }
                    )

                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    SettingsSwitchRow(
                        title = "Dynamic Background",
                        subtitle = "Adapt backdrop gradients to live weather conditions",
                        icon = Icons.Default.Palette,
                        checked = dynamicBackgroundEnabled,
                        onCheckedChange = onToggleDynamicBg
                    )
                }

                // SECTION 4: NOTIFICATIONS
                SettingsSection(title = "NOTIFICATIONS") {
                    SettingsSwitchRow(
                        title = "Master Weather Alerts",
                        subtitle = "Receive notifications for active alerts & critical warnings",
                        icon = Icons.Default.NotificationsActive,
                        checked = notificationsEnabled,
                        onCheckedChange = onToggleNotifications
                    )

                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    SettingsSwitchRow(
                        title = "Rain & Downpour Warnings",
                        subtitle = "Get alerted 15 minutes before precipitation begins",
                        icon = Icons.Default.WaterDrop,
                        checked = rainNotifications,
                        enabled = notificationsEnabled,
                        onCheckedChange = onToggleRainNotifs
                    )

                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    SettingsSwitchRow(
                        title = "Daily Weather Briefing",
                        subtitle = "Morning forecast delivered at 7:30 AM",
                        icon = Icons.Default.WbSunny,
                        checked = dailyBriefing,
                        enabled = notificationsEnabled,
                        onCheckedChange = onToggleDailyBriefing
                    )
                }

                // SECTION 5: LOCATION & SYNC
                SettingsSection(title = "LOCATION & DATA") {
                    SettingsClickableRow(
                        title = "Saved Locations",
                        subtitle = "$savedCitiesCount cities pinned",
                        icon = Icons.Default.Bookmarks,
                        onClick = onOpenSavedCities
                    )

                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    SettingsClickableRow(
                        title = "Telemetry Refresh Interval",
                        subtitle = "Interval: $refreshInterval",
                        icon = Icons.Default.Sync,
                        onClick = { showRefreshDialog = true }
                    )
                }

                // SECTION 6: OTHER & LEGAL
                SettingsSection(title = "ABOUT & LEGAL") {
                    SettingsClickableRow(
                        title = "Application Language",
                        subtitle = language,
                        icon = Icons.Default.Language,
                        onClick = { showLanguageDialog = true }
                    )

                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    SettingsClickableRow(
                        title = "Privacy Policy",
                        subtitle = "How telemetry and location data are handled",
                        icon = Icons.Default.Security,
                        onClick = { showPrivacyDialog = true }
                    )

                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    SettingsClickableRow(
                        title = "Open Source Licenses",
                        subtitle = "Kotlin, Jetpack Compose, Ktor, OkHttp",
                        icon = Icons.Default.Code,
                        onClick = { showLicensesDialog = true }
                    )

                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Version",
                            style = MaterialTheme.typography.bodyMedium,
                            color = glassColors.textSecondary
                        )
                        Text(
                            text = "1.0.0 (Production)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }

                // SECTION 7: DEVELOPER CREDIT — VERY IMPORTANT (Matches requirement #9 strictly)
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = if (glassColors.isDark) Color(0xFF141926).copy(alpha = 0.85f) else Color.White.copy(alpha = 0.75f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (glassColors.isDark) Color.White.copy(alpha = 0.14f) else Color.Black.copy(alpha = 0.08f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Designed & Developed with ❤️ by Aashu",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            ),
                            color = if (glassColors.isDark) Color.White else Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Glass Sky",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 1.2.sp
                            ),
                            color = Color(0xFF38BDF8),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Dialogs
        if (showPrivacyDialog) {
            AlertDialog(
                onDismissRequest = { showPrivacyDialog = false },
                title = { Text("Privacy Policy", color = glassColors.textPrimary) },
                text = {
                    Text(
                        text = "Glass Sky respects your personal privacy. GPS location telemetry is utilized solely on your local device to fetch accurate meteorological forecasts from Open-Meteo. No identifying location or search history is ever transmitted to third-party ad networks or tracking brokers.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = glassColors.textSecondary
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showPrivacyDialog = false }) {
                        Text("Close", color = Color(0xFF38BDF8))
                    }
                },
                containerColor = Color(0xFF181F30)
            )
        }

        if (showLicensesDialog) {
            AlertDialog(
                onDismissRequest = { showLicensesDialog = false },
                title = { Text("Open Source Licenses", color = glassColors.textPrimary) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("• Android Jetpack & Compose (Apache 2.0)", color = glassColors.textSecondary)
                        Text("• Kotlin Coroutines & Serialization (Apache 2.0)", color = glassColors.textSecondary)
                        Text("• Ktor HTTP Client & OkHttp (Apache 2.0)", color = glassColors.textSecondary)
                        Text("• Open-Meteo Weather & Air Quality API (ODbL 1.0)", color = glassColors.textSecondary)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLicensesDialog = false }) {
                        Text("Done", color = Color(0xFF38BDF8))
                    }
                },
                containerColor = Color(0xFF181F30)
            )
        }

        if (showLanguageDialog) {
            val languages = listOf("English", "Español", "Français", "Deutsch", "Hindi", "日本語")
            AlertDialog(
                onDismissRequest = { showLanguageDialog = false },
                title = { Text("Select Language", color = glassColors.textPrimary) },
                text = {
                    Column {
                        languages.forEach { lang ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelectLanguage(lang)
                                        showLanguageDialog = false
                                    }
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(lang, color = glassColors.textPrimary)
                                if (language == lang) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF38BDF8))
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                containerColor = Color(0xFF181F30)
            )
        }

        if (showRefreshDialog) {
            val intervals = listOf("15m", "30m", "1h", "3h", "Manual")
            AlertDialog(
                onDismissRequest = { showRefreshDialog = false },
                title = { Text("Refresh Interval", color = glassColors.textPrimary) },
                text = {
                    Column {
                        intervals.forEach { interval ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelectRefreshInterval(interval)
                                        showRefreshDialog = false
                                    }
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(interval, color = glassColors.textPrimary)
                                if (refreshInterval == interval) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF38BDF8))
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                containerColor = Color(0xFF181F30)
            )
        }

        if (showIntensityDialog) {
            val intensities = listOf("Subtle", "Normal", "Vivid")
            AlertDialog(
                onDismissRequest = { showIntensityDialog = false },
                title = { Text("Animation Intensity", color = glassColors.textPrimary) },
                text = {
                    Column {
                        intensities.forEach { intensity ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelectIntensity(intensity)
                                        showIntensityDialog = false
                                    }
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(intensity, color = glassColors.textPrimary)
                                if (animationIntensity == intensity) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF38BDF8))
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                containerColor = Color(0xFF181F30)
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    val glassColors = LocalGlassColors.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = glassColors.textMuted,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                content = content
            )
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    val glassColors = LocalGlassColors.current
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
                tint = if (enabled) Color(0xFF38BDF8) else glassColors.textMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (enabled) glassColors.textPrimary else glassColors.textMuted
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = glassColors.textMuted
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF38BDF8)
            )
        )
    }
}

@Composable
private fun SettingsClickableRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val glassColors = LocalGlassColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
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
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = glassColors.textPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = glassColors.textMuted
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = glassColors.textMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun UnitChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val bg = if (isSelected) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.08f)
    val col = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)

    Box(
        modifier = Modifier
            .clip(shape)
            .background(bg)
            .border(1.dp, Color.White.copy(alpha = if (isSelected) 0.3f else 0.1f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = col
        )
    }
}

@Composable
private fun ThemeOptionCard(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current

    val bg = if (isSelected) {
        if (glassColors.isDark) Color(0xFF38BDF8).copy(alpha = 0.25f) else Color(0xFF0284C7).copy(alpha = 0.18f)
    } else {
        if (glassColors.isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.45f)
    }

    val borderCol = if (isSelected) {
        Color(0xFF38BDF8)
    } else {
        Color.Transparent
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(1.dp, borderCol, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Color(0xFF38BDF8) else glassColors.textSecondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) glassColors.textPrimary else glassColors.textSecondary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
