package com.adriancruz.oldbalance.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class TabItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : TabItem("home", Icons.Default.ShowChart, "Progreso")
    object Add : TabItem("add", Icons.Default.AddCircle, "Registrar")
    object History : TabItem("history", Icons.Default.List, "Historial")
    object Goals : TabItem("goals", Icons.Default.Flag, "Objetivos")
}
