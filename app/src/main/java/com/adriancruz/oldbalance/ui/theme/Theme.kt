package com.adriancruz.oldbalance.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HealthyColors = lightColors(
    primary = PrimaryBlue,
    primaryVariant = PrimaryBlueVariant,
    secondary = ProgressOrange,
    error = AlertRed,
    background = LightBackground,
    surface = SurfaceWhite,
    onPrimary = OnPrimary,
    onSecondary = Color.Black,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onError = Color.White
)

@Composable
fun OldBalanceTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = HealthyColors // For now, we only support the light theme as per the design

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
