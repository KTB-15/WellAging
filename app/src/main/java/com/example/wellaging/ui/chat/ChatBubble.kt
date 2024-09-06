package com.example.wellaging.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wellaging.FontSizeViewModel

@Composable
fun ChatBubble(message: String, isUser: Boolean, fontSizeAdjustment: Float, fontSizeViewModel: FontSizeViewModel) {
    val stepTextSize = (28f + fontSizeViewModel.fontSizeAdjustment.value).sp
    val labelTextSize = (20f + fontSizeViewModel.fontSizeAdjustment.value).sp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AI",
                    color = Color.White,
                    fontSize = labelTextSize
                )
            }
        }

        Box(
            modifier = Modifier
                .background(
                    color = if (isUser) Color(red = 255, green = 65, blue = 145) else Color(0xFFFFE0E0),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
                .widthIn(max = 250.dp)
        ) {
            Text(
                text = message,
                color = if (isUser) Color.White else Color.Black,
                fontSize = labelTextSize // 글자 크기 조정
            )
        }

        if (isUser) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFA500)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "나",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = (16f + fontSizeAdjustment).sp // 글자 크기 조정
                )
            }
        }
    }
}
