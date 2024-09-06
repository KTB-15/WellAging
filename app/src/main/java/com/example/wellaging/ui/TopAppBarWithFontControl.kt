package com.example.wellaging.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithFontControl(
    onFontSizeIncrease: () -> Unit,
    onFontSizeDecrease: () -> Unit
) {
    TopAppBar(
        title = { // 여기 괄호 추가
            Text(
                "WellAging",
                fontSize = 24.sp,
                color = Color.White // 글자 색을 흰색으로
            )
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "가",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier
                        .clickable { onFontSizeDecrease() }
                        .padding(horizontal = 8.dp)
                )

                Text(
                    text = "가",
                    fontSize = 24.sp,
                    color = Color.White,
                    modifier = Modifier
                        .clickable { onFontSizeIncrease() }
                        .padding(horizontal = 8.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(red = 255, green = 65, blue = 145) // 상단바 배경을 핑크로 변경
        )
    )
}