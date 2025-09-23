package com.adriancruz.oldbalance.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HealthyColors = lightColors(
    primary = AppColors.PrimaryBlue,
    primaryVariant = AppColors.PrimaryBlueVariant,
    secondary = AppColors.ProgressOrange,
    error = AppColors.AlertRed,
    background = AppColors.LightBackground,
    surface = AppColors.SurfaceWhite,
    onPrimary = AppColors.OnPrimary,
    onSecondary = Color.Black,
    onBackground = AppColors.TextPrimary,
    onSurface = AppColors.TextPrimary,
    onError = Color.White
)

@Composable
fun OldBalanceTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colors = HealthyColors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
