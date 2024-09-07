package com.example.wellaging.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wellaging.FontSizeViewModel
import com.example.wellaging.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun History(
    navController: NavController,
    fontSizeViewModel: FontSizeViewModel
) {
    var showDetail by remember { mutableStateOf(false) }  // 상세 화면을 보여줄지 여부를 결정하는 상태 변수
    var selectedQuestion by remember { mutableStateOf("") }
    var selectedAnswer by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableStateOf("김치찌개") }  // 예시로 김치찌개를 정답으로 설정

    // 미리 정해진 질문과 사용자의 답변, 정답 여부
    val chatHistory = listOf(
        Triple("오늘 아침에 뭐 드셨어요?", "가지볶음", false),   // 오답 (빨간색)
        Triple("어제 어디 다녀오셨어요?", "슈퍼마켓", true),    // 정답 (초록색)
        Triple("요즘 즐겨보는 TV 프로그램이 뭐에요?", "뉴스", false), // 오답 (빨간색)
        Triple("오늘 날씨가 어때요?", "맑아요", true)            // 정답 (초록색)
    )

    if (showDetail) {
        // 상세 화면을 보여줌
        AnswerDetailScreen(
            question = selectedQuestion,
            userAnswer = selectedAnswer,
            correctAnswer = correctAnswer,
            onBack = { showDetail = false },
            fontSizeViewModel// 뒤로가기 버튼 클릭 시 다시 목록 화면으로 전환
        )
    } else {
        // 목록 화면
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
                    items(chatHistory) { (question, answer, isCorrect) ->
                        ChatBubbleWithAnswerCheck(
                            question = question,
                            isCorrect = isCorrect,
                            onClick = {
                                if (!isCorrect) {
                                    selectedQuestion = question
                                    selectedAnswer = answer
                                    showDetail = true  // 오답을 클릭하면 상세 화면으로 전환
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnswerDetailScreen(
    question: String,
    userAnswer: String,
    correctAnswer: String,
    onBack: () -> Unit,  // 뒤로 가기 이벤트
    fontSizeViewModel: FontSizeViewModel

) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("오답", fontSize = fontSizeViewModel.fontSizeAdjustment.value.sp, fontWeight = FontWeight.Bold, color = Color.Red)
            Spacer(modifier = Modifier.height(16.dp))
            Text("질문: $question", fontSize = fontSizeViewModel.fontSizeAdjustment.value.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("내가 한 답변: $userAnswer", fontSize = fontSizeViewModel.fontSizeAdjustment.value.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("정답: $correctAnswer", fontSize = fontSizeViewModel.fontSizeAdjustment.value.sp, fontWeight = FontWeight.Medium, color = Color.Green)

            // 뒤로 가기 버튼
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onBack) {
                Text("뒤로가기")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBubbleWithAnswerCheck(
    question: String,
    isCorrect: Boolean,
    onClick: () -> Unit
) {
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
            .clickable(onClick = onClick)  // 클릭 이벤트 추가
    ) {
        Text(text = question, color = Color.White)
    }
}



