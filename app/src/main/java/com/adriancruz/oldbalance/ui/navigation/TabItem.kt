package com.adriancruz.oldbalance.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector

sealed class TabItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : TabItem("home", Icons.Filled.TrendingUp, "Progreso")
    object Add : TabItem("add", Icons.Default.AddCircle, "Registrar")
    object History : TabItem("history", Icons.Default.List, "Historial")
    object Goals : TabItem("goals", Icons.Filled.Flag, "Objetivos")
    object Developer : TabItem("developer", Icons.Default.Build, "Developer")
}
