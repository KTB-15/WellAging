package com.example.wellaging

import android.os.Bundle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var fontSizeAdjustment by remember { mutableStateOf(0f) }

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "savings_progress") {
        composable("savings_progress") {
            Scaffold(
                topBar = {
                    Column {
                        // 상단바
                        TopAppBarWithFontControl(
                            onFontSizeIncrease = {
                                if (fontSizeAdjustment < 12f) {
                                    fontSizeAdjustment += 4f
                                }
                            },
                            onFontSizeDecrease = {
                                if (fontSizeAdjustment > -8f) {
                                    fontSizeAdjustment -= 4f
                                }
                            }
                        )

                        // 상단바 밑에 광고 배너 추가
                        Image(
                            painter = painterResource(id = R.drawable.images), // drawable에 있는 이미지로 설정
                            contentDescription = "Ad Banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth() // 화면 가득 채우기
                                .height(60.dp) // 높이는 30dp
                        )
                    }
                },
                bottomBar = { BottomNavigation(navController = navController) }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SavingsProgress(fontSizeAdjustment)
                }
            }
        }
        composable("chat_screen") {
            ChatScreen(navController)
        }
    }
}

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsProgress(fontSizeAdjustment: Float) {
    val totalSteps = 10000f
    val currentSteps = 1876f
    val progressPercentage = currentSteps / totalSteps
    val sections = 4

    val stepTextSize = (28f + fontSizeAdjustment).sp
    val labelTextSize = (20f + fontSizeAdjustment).sp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${currentSteps.toInt()} 걸음",
            fontSize = stepTextSize,
            fontWeight = FontWeight.Bold,
            color = Color(red=255, green=65, blue=145)
        )

        Spacer(modifier = Modifier.height(16.dp))

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            val barWidth = constraints.maxWidth.toFloat()
            val iconOffsetX = (barWidth * progressPercentage).coerceIn(0f, barWidth)

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(24.dp)) {
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = size.height
                    )

                    drawLine(
                        color = Color(red=255, green=65, blue=145),
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width * progressPercentage, size.height / 2),
                        strokeWidth = size.height
                    )

                    val sectionWidth = size.width / sections
                    for (i in 1 until sections) {
                        drawLine(
                            color = Color.Black,
                            start = Offset(sectionWidth * i, 0f),
                            end = Offset(sectionWidth * i, size.height),
                            strokeWidth = 3f
                        )
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.walkingman),
                    contentDescription = "Running Person",
                    modifier = Modifier
                        .offset(x = with(LocalDensity.current) { iconOffsetX.toDp() - 12.dp }, y = (-16).dp)
                        .size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "0", fontSize = labelTextSize)
            Text(text = "2500", fontSize = labelTextSize)
            Text(text = "5000", fontSize = labelTextSize)
            Text(text = "7500", fontSize = labelTextSize)
            Text(text = "10000", fontSize = labelTextSize)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavHostController) {
    var fontSizeAdjustment by remember { mutableStateOf(0f) }
    var messages by remember { mutableStateOf(listOf<Pair<String, Boolean>>()) }
    var isMicActive by remember { mutableStateOf(false) }

    fun addMessage(message: String, isUser: Boolean) {
        messages = messages + Pair(message, isUser)
    }

    fun onMicClick() {
        if (isMicActive) {
            addMessage("응 먹었어", true)
        } else {
            addMessage("점심 뭐 먹었나요?", false)
        }
        isMicActive = !isMicActive
    }

    Scaffold(
        topBar = {
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
                                        fontSizeAdjustment -= 3f
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
                                        fontSizeAdjustment += 4f
                                    }
                                }
                                .padding(horizontal = 8.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(red = 255, green = 65, blue = 145) // 상단바 배경 색상
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Top
            ) {
                messages.forEach { (message, isUser) ->
                    ChatBubble(message = message, isUser = isUser, fontSizeAdjustment = fontSizeAdjustment)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(red = 255, green = 65, blue = 145))
                        .clickable { onMicClick() },
                    contentAlignment = Alignment.Center
                ) {
                    if (isMicActive) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop Recording",
                            tint = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBubble(message: String, isUser: Boolean, fontSizeAdjustment: Float) {
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
                    fontSize = (16f + fontSizeAdjustment).sp // 글자 크기 조정
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
                fontSize = (16f + fontSizeAdjustment).sp // 글자 크기 조정
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


@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    MainScreen()
}
