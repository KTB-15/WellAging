package com.example.wellaging.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wellaging.ui.theme.Purple40
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class ChatMessage(
    val text: String,
    val isUser: Boolean = false
)

@Composable
fun ChatList(messages: List<ChatMessage>) {
    LazyColumn {
        items(messages) { message ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
            ) {
                ChatTextBox(message = message.text)
            }
        }
    }
}

@Composable
fun ChatTextBox(message: String) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = Purple40,
        tonalElevation = 1.dp,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = message,
            fontSize = 28.sp,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun InputContainer(
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    recognizedText: String,
    onChatSubmit: (String) -> Unit,
    inputText: String,
    onInputTextChange: (String) -> Unit,
    isWaitingForResponse: Boolean
) {
    var isListening by remember { mutableStateOf(false) }

    LaunchedEffect(recognizedText) {
        if (recognizedText.isNotEmpty()) {
            onInputTextChange(recognizedText)
        }
    }

    Column {
        SpeechRecognitionButton(
            isListening = isListening,
            onStartListening = {
                onStartListening()
                isListening = true
            },
            onStopListening = {
                onStopListening()
                isListening = false
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(56.dp)
        ) {
            TextField(
                value = inputText,
                onValueChange = onInputTextChange,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(32.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedIndicatorColor = Color.Transparent,  // 밑줄 제거
                    focusedIndicatorColor = Color.Transparent     // 포커스 시 밑줄 제거
                ),
                placeholder = { Text("글자를 입력하세요", fontSize = 20.sp) }
            )

            Spacer(modifier = Modifier.width(20.dp))

            Button(
                colors = ButtonDefaults.buttonColors(
                    Purple40
                ),
                onClick = {
                    onChatSubmit(inputText)
                },
                enabled = inputText.isNotEmpty() && !isWaitingForResponse
            ) {
                Text("등록", fontSize = 28.sp, color = Color.Black)
            }
        }
    }
}

@Composable
fun SpeechRecognitionButton(
    isListening: Boolean,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit
) {
    Button(
        onClick = if (isListening) onStopListening else onStartListening,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isListening) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
        )
    ) {
        Text(if (isListening) "말하기 종료" else "말하기", fontSize = 28.sp)
    }
}

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

    현재까지의 대화: 안녕하세요! 식사하셨나요?
"""

class ApiTask {
    suspend fun getUserInfo(prompt: String, userInput: String): String = withContext(Dispatchers.IO) {
        val urlString = "http://54.180.249.97:8000/getuserinfo"
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonInputString = JSONObject().apply {
                put("prompt", URLEncoder.encode(prompt, "UTF-8"))
                put("user_input", URLEncoder.encode(userInput, "UTF-8"))
            }.toString()

            connection.outputStream.use { os ->
                val input = jsonInputString.toByteArray(charset("utf-8"))
                os.write(input, 0, input.size)
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                "Error: ${connection.responseCode}"
            }
        } catch (e: Exception) {
            "죄송합니다. 응답을 받아오는 데 문제가 발생했습니다: ${e.message}"
        } finally {
            connection.disconnect()
        }
    }
}