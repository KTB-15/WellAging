package com.example.wellaging.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FontSizeAdjustmentButtons(fontSizeAdjustment: Float, onFontSizeChange: (Float) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "가",
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier
                .clickable {
                    if (fontSizeAdjustment > -6f) {
                        onFontSizeChange(fontSizeAdjustment - 3f)
                    }
                }
                .padding(horizontal = 8.dp)
        )
        Text(
            text = "가",
            fontSize = 24.sp,
            color = Color.White,
            modifier = Modifier
                .clickable {
                    if (fontSizeAdjustment < 12f) {
                        onFontSizeChange(fontSizeAdjustment + 4f)
                    }
                }
                .padding(horizontal = 8.dp)
        )
    }
}
