package com.example.wellaging

sealed class Screens(val route : String) {
    object Home : Screens("home_screen")
    object Chat : Screens("chat_screen")
    object History : Screens("history_screen")
    object Pay : Screens("pay_screen")
    object Quiz : Screens("quiz_screen")
}