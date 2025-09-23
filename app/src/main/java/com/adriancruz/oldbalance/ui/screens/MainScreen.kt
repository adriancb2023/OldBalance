package com.adriancruz.oldbalance.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.adriancruz.oldbalance.ui.navigation.TabItem
import com.adriancruz.oldbalance.ui.viewmodel.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            val items = listOf(TabItem.Home, TabItem.Add, TabItem.History, TabItem.Goals)
            BottomNavigation(backgroundColor = MaterialTheme.colors.primary) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                items.forEach { item ->
                    BottomNavigationItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selectedContentColor = Color.White,
                        unselectedContentColor = Color.White.copy(0.6f),
                        alwaysShowLabel = true,
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) {
                                        saveState = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = TabItem.Home.route, Modifier.padding(innerPadding)) {
            composable(TabItem.Home.route) { HomeScreen(viewModel) }
            composable(TabItem.Add.route) { AddEntryScreen(viewModel) }
            composable(TabItem.History.route) { HistoryScreen(viewModel) }
            composable(TabItem.Goals.route) { GoalsScreen(viewModel) }
        }
    }
}
