package com.example.wellaging.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wellaging.FontSizeViewModel

@Composable
fun History(
    navController: NavController,
    fontSizeViewModel: FontSizeViewModel
) {
    // 폰트사이즈 적용 테스트
    val stepTextSize = (28f + fontSizeViewModel.fontSizeAdjustment.value).sp

    Text(
        text = "걸음",
        fontSize = stepTextSize,
    )
}