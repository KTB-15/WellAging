package com.example.wellaging.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wellaging.FontSizeViewModel
import com.example.wellaging.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun History(
    navController: NavController,
    fontSizeViewModel: FontSizeViewModel
) {// 미리 정해진 질문과 정답 여부 (true는 정답, false는 오답)
    val chatHistory = listOf(
        "오늘 아침에 뭐 드셨어요?" to false,   // 오답 (빨간색)
        "어제 어디 다녀오셨어요?" to true,   // 정답 (초록색)
        "요즘 즐겨보는 TV 프로그램이 뭐에요?" to false, // 오답 (빨간색)
        "오늘 날씨가 어때요?" to true         // 정답 (초록색)
    )

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Top
            ) {
                items(chatHistory) { (message, isCorrect) ->
                    ChatBubbleWithAnswerCheck(
                        question = message,
                        isCorrect = isCorrect
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubbleWithAnswerCheck(question: String, isCorrect: Boolean) {
    val backgroundColor = if (isCorrect) {
        Color(0xFF66BB6A) // 초록색 (정답)
    } else {
        Color(0xFFEF5350) // 빨간색 (오답)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                color = backgroundColor,
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        Text(text = question, color = Color.White)
    }
}

@Preview
@Composable
fun PreviewChatExample() {
    ChatExample()
}