package com.example.wellaging

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

//initializing the data class with default parameters
data class BottomNavigationItem(
    val label : String = "",
    val icon : ImageVector = Icons.Filled.Home,
    val route : String = ""
) {

    //function to get the list of bottomNavigationItems
    fun bottomNavigationItems() : List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = "Chat",
                icon = Icons.Filled.Search,
                route = Screens.Chat.route
            ),
            BottomNavigationItem(
                label = "Quiz",
                icon = Icons.Filled.AccountCircle,
                route = Screens.Quiz.route
            ),
            BottomNavigationItem(
                label = "Home",
                icon = Icons.Filled.Home,
                route = Screens.Home.route
            ),
            BottomNavigationItem(
                label = "Pay",
                icon = Icons.Filled.AccountCircle,
                route = Screens.Pay.route
            ),
            BottomNavigationItem(
                label = "History",
                icon = Icons.Filled.AccountCircle,
                route = Screens.History.route
            ),
        )
    }
}