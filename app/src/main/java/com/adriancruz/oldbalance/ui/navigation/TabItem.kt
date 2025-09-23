package com.adriancruz.oldbalance.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class TabItem(val route: String, val icon: ImageVector, val title: String) {
    // Reemplazamos ShowChart por TrendingUp (o InsertChart)
    object Home : TabItem("home", Icons.Filled.TrendingUp, "Progreso")
    object Add : TabItem("add", Icons.Default.AddCircle, "Registrar")
    object History : TabItem("history", Icons.Default.List, "Historial")
    object Goals : TabItem("goals", Icons.Filled.Flag, "Objetivos")
}
