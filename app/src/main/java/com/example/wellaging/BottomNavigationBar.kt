package com.example.wellaging

import com.example.wellaging.screens.Chat
import com.example.wellaging.screens.History
import com.example.wellaging.screens.Home
import com.example.wellaging.screens.Pay
import com.example.wellaging.screens.Quiz
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun BottomNavigationBar(fontSizeViewModel: FontSizeViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                BottomNavigationItem().bottomNavigationItems().forEachIndexed { _, navigationItem ->
                    NavigationBarItem(
                        selected = navigationItem.route == currentDestination?.route,
                        label = {
                            Text(navigationItem.label)
                        },
                        icon = {
                            Icon(
                                navigationItem.icon,
                                contentDescription = navigationItem.label
                            )
                        },
                        onClick = {
                            navController.navigate(navigationItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) {paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screens.Home.route,
            modifier = Modifier.padding(paddingValues = paddingValues)) {
            composable(Screens.Home.route) {
                Home(
                    navController,
                    fontSizeViewModel
                )
            }
            composable(Screens.History.route) {
                History(
                    navController,
                    fontSizeViewModel
                )
            }
            composable(Screens.Chat.route) {
                Chat(
                    navController,
                    fontSizeViewModel
                )
            }
            composable(Screens.Pay.route) {
                Pay(
                    navController,
                    fontSizeViewModel
                )
            }
            composable(Screens.Quiz.route) {
                Quiz(
                    navController,
                    fontSizeViewModel
                )
            }

        }
    }
}