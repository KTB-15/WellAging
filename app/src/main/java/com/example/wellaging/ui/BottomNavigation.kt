package com.example.wellaging.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigation(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Default.AttachMoney,
            contentDescription = "Money",
            tint = Color.Gray
        )
        Icon(
            imageVector = Default.Home,
            contentDescription = "Home",
            tint = Color.Gray
        )
        Icon(
            imageVector = Default.Chat,
            contentDescription = "대화하기",
            tint = Color.Gray,
            modifier = Modifier.clickable {
                navController.navigate("chat_screen") // 간단히 네비게이션 처리
            }
        )
    }
}
