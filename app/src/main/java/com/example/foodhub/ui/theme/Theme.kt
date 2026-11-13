package com.example.foodhub.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = GreenContainer,
    onPrimaryContainer = GreenDark,
    secondary = OrangeSecondary,
    onSecondary = Color.White,
    secondaryContainer = OrangeContainer,
    onSecondaryContainer = OrangeSecondary,
    background = BackgroundLight,
    surface = SurfaceWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun FoodHubTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
