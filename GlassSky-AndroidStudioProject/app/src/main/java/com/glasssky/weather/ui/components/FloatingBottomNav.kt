package com.glasssky.weather.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.glasssky.weather.ui.theme.LocalGlassColors

enum class BottomNavTab {
    HOME,
    SEARCH,
    NOTIFICATIONS,
    MAP
}

/**
 * Premium Floating Glass Bottom Navigation Bar matching the reference image.
 */
@Composable
fun FloatingBottomNav(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current
    val navShape = RoundedCornerShape(36.dp)

    val navBg = if (glassColors.isDark) {
        Color(0xF2121724)
    } else {
        Color(0xF2FFFFFF)
    }

    val navBorder = if (glassColors.isDark) {
        Color.White.copy(alpha = 0.16f)
    } else {
        Color.Black.copy(alpha = 0.08f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = navShape,
            color = navBg,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .border(1.dp, navBorder, navShape)
                .shadow(elevation = 16.dp, shape = navShape, spotColor = Color.Black.copy(alpha = 0.35f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavTabItem(
                    icon = Icons.Default.Home,
                    isSelected = selectedTab == BottomNavTab.HOME,
                    onClick = { onTabSelected(BottomNavTab.HOME) }
                )
                NavTabItem(
                    icon = Icons.Default.Search,
                    isSelected = selectedTab == BottomNavTab.SEARCH,
                    onClick = { onTabSelected(BottomNavTab.SEARCH) }
                )
                NavTabItem(
                    icon = Icons.Default.Notifications,
                    isSelected = selectedTab == BottomNavTab.NOTIFICATIONS,
                    onClick = { onTabSelected(BottomNavTab.NOTIFICATIONS) }
                )
                NavTabItem(
                    icon = Icons.Default.Map,
                    isSelected = selectedTab == BottomNavTab.MAP,
                    onClick = { onTabSelected(BottomNavTab.MAP) }
                )
            }
        }
    }
}

@Composable
private fun NavTabItem(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val glassColors = LocalGlassColors.current

    val iconTint by animateColorAsState(
        targetValue = if (isSelected) {
            if (glassColors.isDark) Color.White else Color(0xFF0F172A)
        } else {
            if (glassColors.isDark) Color.White.copy(alpha = 0.40f) else Color(0xFF64748B)
        },
        animationSpec = tween(durationMillis = 300),
        label = "NavIconTint"
    )

    val activePillBg by animateColorAsState(
        targetValue = if (isSelected) {
            if (glassColors.isDark) Color.White.copy(alpha = 0.14f) else Color.Black.copy(alpha = 0.08f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = 300),
        label = "NavPillBg"
    )

    Box(
        modifier = Modifier
            .size(width = 52.dp, height = 44.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(activePillBg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
    }
}
