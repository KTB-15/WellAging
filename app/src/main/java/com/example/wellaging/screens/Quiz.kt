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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.wellaging.FontSizeViewModel
import com.example.wellaging.model.ChatViewModel
import com.example.wellaging.ui.chat.ChatBubble
import com.example.wellaging.ui.chat.MicButton
import com.example.wellaging.ui.component.ApiTask
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun Quiz(
    navController: NavHostController,
    fontSizeViewModel: FontSizeViewModel,
    viewModel: ChatViewModel = viewModel()
) {
    val stepTextSize = (28f + fontSizeViewModel.fontSizeAdjustment.value).sp
    val labelTextSize = (20f + fontSizeViewModel.fontSizeAdjustment.value).sp

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
    val context = LocalContext.current
    val ttsWrapper = rememberTtsWrapper(context)

    val addMessage = remember {
        { message: String, isUser: Boolean ->
            messages = messages + Pair(message, isUser)
            if (isUser) {
                isWaitingForAiResponse = true
                coroutineScope.launch {
                    try {
                        val aiResponse = apiTask.getUserInfo(accumulatedChat, message)
                        val aiMessage = JSONObject(aiResponse).getString("body")
                        messages = messages + Pair(aiMessage, false)
                        accumulatedChat += "어르신: $message 당신: $aiMessage "
                        Log.d("누적 텍스트??", accumulatedChat)
                        ttsWrapper.speakText(aiMessage)
                    } catch (e: Exception) {
                        val errorMessage = "죄송합니다. 오류가 발생했습니다: ${e.message}"
                        messages = messages + Pair(errorMessage, false)
                        ttsWrapper.speakText(errorMessage)
                    } finally {
                        isWaitingForAiResponse = false
                    }
                }
            } else {
                accumulatedChat += "당신: $message "
                ttsWrapper.speakText(message)
            }
            Log.d("누적 텍스트", accumulatedChat)
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
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Top
            ) {
                items(messages) { (message, isUser) ->
                    ChatBubble(
                        message = message,
                        isUser = isUser,
                        fontSizeAdjustment,
                        fontSizeViewModel = fontSizeViewModel
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

            MicButton(isListening = isListening, onMicClick = { onMicClick() })
        }
    }

}