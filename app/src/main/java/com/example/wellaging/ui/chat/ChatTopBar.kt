package com.example.wellaging.ui.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(navController: NavController, fontSizeAdjustment: Float, onFontSizeChange: (Float) -> Unit) {
    TopAppBar(
        title = { // title 부분을 람다로 감쌈
            Text(
                "대화하기",
                color = Color.White // 글씨 색상 흰색
            )
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(16.dp) // 여기에 패딩 추가
            )
        },
        actions = {
            FontSizeAdjustmentButtons(fontSizeAdjustment = fontSizeAdjustment, onFontSizeChange = onFontSizeChange)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(red = 255, green = 65, blue = 145) // 상단바 배경 색상
        )
    )
}
