package com.glasssky.weather.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var startAnimation by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1.0f else 0.65f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "SplashScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 750, easing = LinearOutSlowInEasing),
        label = "SplashAlpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        // Allow smooth viewing transition without delaying app start
        delay(1250)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF040814),
                        Color(0xFF0B1426),
                        Color(0xFF14243F)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Atmospheric background glow aura
        Box(
            modifier = Modifier
                .size(240.dp)
                .scale(scale * 1.15f)
                .alpha(alpha * 0.45f)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0xFF38BDF8).copy(alpha = 0.35f),
                            Color(0xFF818CF8).copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scale)
                .alpha(alpha)
        ) {
            // Glass Emblem
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.18f),
                                Color.White.copy(alpha = 0.06f)
                            )
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.45f),
                                Color.White.copy(alpha = 0.10f)
                            )
                        ),
                        shape = RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CloudQueue,
                    contentDescription = "Glass Sky Logo",
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Name
            Text(
                text = "Glass Sky",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Atmospheric Weather Experience",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.65f),
                letterSpacing = 0.5.sp
            )
        }
    }
}
