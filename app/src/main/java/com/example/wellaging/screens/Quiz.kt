package com.example.wellaging.screens

import android.Manifest
import android.content.Context
import android.speech.tts.TextToSpeech
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
import androidx.compose.runtime.DisposableEffect
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
import com.example.wellaging.ui.component.ChatItem
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

// 전역 퀴즈
var quizs: MutableList<ChatItem> = mutableListOf()

@Composable
fun Quiz(
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
    var currentQuizIndex by remember { mutableStateOf(0) }
    var isWaitingForAiResponse by remember { mutableStateOf(false) }
    var isChatEnded by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val ttsWrapper = rememberTtsWrapper(context)
    val apiTask = remember { ApiTask() }

    val addMessage = remember {
        { message: String, isUser: Boolean ->
            messages = messages + Pair(message, isUser)
            if (!isUser) {
                ttsWrapper.speakText(message)
            }
        }
    }
    var currentAnswer = ""

    LaunchedEffect(Unit) {
        viewModel.checkAndRequestAudioPermission()
        if (quizs.isNotEmpty()) {
            addMessage(quizs[currentQuizIndex].Q, false)
        }
    }
    LaunchedEffect(recognizedText) {
        if (recognizedText.isNotEmpty() && !isListening) {
            addMessage(recognizedText, true)
            Log.d("IMPORTANT KTB!?!?", recognizedText)
            isWaitingForAiResponse = true
            coroutineScope.launch {
                try {
                    Log.d("IMPORTANT KTB", quizs[currentQuizIndex].A)
                    Log.d("IMPORTANT KTB", recognizedText)
                    val response = apiTask.checkAnswer(quizs[currentQuizIndex].A, recognizedText)
                    addMessage(response.result, false)

                    currentQuizIndex++
                    if (currentQuizIndex < quizs.size) {
                        addMessage(quizs[currentQuizIndex].Q, false)
                    } else {
                        addMessage("모든 퀴즈가 끝났습니다. 수고하셨습니다!", false)
                    }
                } catch (e: Exception) {
                    addMessage("죄송합니다. 오류가 발생했습니다: ${e.message}", false)
                } finally {
                    isWaitingForAiResponse = false
                    viewModel.clearRecognizedText()
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        viewModel.onPermissionResult(isGranted)
    }

    if (permissionNeeded) {
        LaunchedEffect(permissionNeeded) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun onMicClick() {
        when {
            permissionGranted && !isWaitingForAiResponse && !isChatEnded -> {
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
                        fontSizeAdjustment = fontSizeAdjustment,
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

            MicButton(isListening = isListening, onMicClick = { onMicClick() }, enabled = !isChatEnded && permissionGranted && !isWaitingForAiResponse)
        }
    }

}