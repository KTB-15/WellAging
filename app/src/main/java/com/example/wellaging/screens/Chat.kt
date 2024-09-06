package com.example.wellaging.screens

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.wellaging.FontSizeViewModel
import com.example.wellaging.model.ChatViewModel
import com.example.wellaging.ui.chat.ChatBubble
import com.example.wellaging.ui.chat.MicButton
import com.example.wellaging.ui.component.ApiTask
import kotlinx.coroutines.launch

val TALK_PROMPT = """
    당신은 어르신과 친근하게 대화를 나누는 상대입니다. 
    다음 지침을 따라 자연스럽고 가벼운 대화를 이어가세요:

    질문은 간단하고 친근하게 하되, 일상적인 대화 흐름을 유지하세요.

    다음 주제들을 골고루 다루며 대화를 이어가세요:
    - 일상 활동 및 취미
    - 최근의 경험이나 사건
    - 가족이나 친구 관계
    - 날씨나 계절에 대한 이야기
    - 가벼운 시사 이야기
    - 즐거운 추억

    한 주제에 대해 1-2번 이상 질문하지 마세요. 새로운 주제로 자연스럽게 전환하세요.

    어르신의 답변에 1-2문장으로 짧게 반응하고 즉시 새로운 질문으로 넘어가세요.

    각 응답은 다음 구조를 따르세요:[짧은 반응] + [새로운 주제로의 질문]

    대화의 흐름을 자연스럽게 유지하되, 5개의 서로 다른 주제를 다루도록 하세요.

    * 현재까지의 대화
    
"""

@Composable
fun Chat(
    navController: NavHostController,
    fontSizeViewModel: FontSizeViewModel,
    viewModel: ChatViewModel = viewModel()
) {
    var fontSizeAdjustment by remember { mutableStateOf(0f) }
    var messages by remember { mutableStateOf(listOf<Pair<String, Boolean>>()) }
    val recognizedText by viewModel.recognizedText
    val isListening by viewModel.isListening
    val permissionGranted by viewModel.permissionGranted
    val permissionNeeded by viewModel.permissionNeeded
    var isWaitingForAiResponse by remember { mutableStateOf(false) }

    var accumulatedChat by remember { mutableStateOf(TALK_PROMPT) } // 누적 텍스트

    val apiTask = remember { ApiTask() }
    val coroutineScope = rememberCoroutineScope()

    val addMessage = remember {
        { message: String, isUser: Boolean ->
            Log.d("누적 텍스트", accumulatedChat)
            messages = messages + Pair(message, isUser)
            accumulatedChat += "당신: $message "

            if (isUser) {
                isWaitingForAiResponse = true
                coroutineScope.launch {
                    try {
                        val aiResponse = apiTask.getUserInfo(accumulatedChat, message)
                        messages = messages + Pair(aiResponse, false)
                        accumulatedChat += "어르신: $message"
                    } catch (e: Exception) {
                        val errorMessage = "죄송합니다. 오류가 발생했습니다: ${e.message}"
                        messages = messages + Pair(errorMessage, false)
                    } finally {
                        isWaitingForAiResponse = false
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkAndRequestAudioPermission()
        addMessage("안녕하세요! 식사하셨나요?", false)
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
            permissionGranted && !isWaitingForAiResponse -> {
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Top
        ) {
            items(messages) { (message, isUser) ->
                ChatBubble(
                    message = message,
                    isUser = isUser,
                    fontSizeAdjustment = fontSizeAdjustment
                )
            }
            item {
                if (isWaitingForAiResponse) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

            Spacer(modifier = Modifier.height(16.dp))

            MicButton(isListening = isListening, onMicClick = {onMicClick()})
        }
    }

}