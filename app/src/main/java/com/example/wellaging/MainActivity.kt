package com.example.wellaging

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wellaging.model.ChatViewModel
import com.example.wellaging.ui.BottomNavigation
import com.example.wellaging.ui.ChatBubble
import com.example.wellaging.ui.ChatTopBar
import com.example.wellaging.ui.MicButton
import com.example.wellaging.ui.SavingsProgress
import com.example.wellaging.ui.TopAppBarWithFontControl

class MainActivity : ComponentActivity() {
    // 권한 요청 계약 생성
    private val sensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val sensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) }
    // 권한 요청 계약 생성
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            // 권한이 거부되었을 때의 처리
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

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
            ChatScreen(navController, )
        }
    }
}

@Composable
fun ChatScreen(
    navController: NavHostController,
    viewModel: ChatViewModel = viewModel()
) {
    var fontSizeAdjustment by remember { mutableStateOf(0f) }
    var messages by remember { mutableStateOf(listOf<Pair<String, Boolean>>()) }
    val recognizedText by viewModel.recognizedText
    val isListening by viewModel.isListening
    val permissionGranted by viewModel.permissionGranted
    val permissionNeeded by viewModel.permissionNeeded

    fun addMessage(message: String, isUser: Boolean) {
        messages = messages + Pair(message, isUser)
    }

    LaunchedEffect(Unit) {
        viewModel.checkAndRequestAudioPermission()
        addMessage("안녕하세요~", false)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        viewModel.onPermissionResult(isGranted)
    }

    LaunchedEffect(recognizedText) {
        if (recognizedText.isNotEmpty() && !isListening) {
            addMessage(recognizedText, true)
            viewModel.clearRecognizedText()
        }
    }

    fun onMicClick() {
        when {
            permissionGranted -> {
                if (isListening) {
                    viewModel.stopListening()
                } else {
                    viewModel.startListening()
                }
            }
            permissionNeeded -> {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }
    if (permissionNeeded) {
        LaunchedEffect(permissionNeeded) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }


    Scaffold(
        topBar = {
            ChatTopBar(navController = navController, fontSizeAdjustment = fontSizeAdjustment, onFontSizeChange = {
                fontSizeAdjustment = it
            })
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
                    ChatBubble(
                        message = message,
                        isUser = isUser,
                        fontSizeAdjustment = fontSizeAdjustment
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            MicButton(isListening = isListening, onMicClick = {onMicClick()})
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    MainScreen()
}