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
import org.json.JSONArray
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

data class ChatItem(val Q: String, val A: String)

class ApiTask {

    // 문제 얻기
    suspend fun makeQnA(chatHistory: List<ChatItem>): String = withContext(Dispatchers.IO) {
        val urlString = "http://54.180.249.97:8000/makeqna"
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonInputString = JSONObject().apply {
                put("chat_history", JSONArray().apply {
                    chatHistory.forEach { chatItem ->
                        put(JSONObject().apply {
                            put("Q", chatItem.Q)
                            put("A", chatItem.A)
                        })
                    }
                })
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

    suspend fun getUserInfo(prompt: String, userInput: String): String = withContext(Dispatchers.IO) {
        val urlString = "http://54.180.249.97:8000/getuserinfo"
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonInputString = JSONObject().apply {
                put("prompt", prompt)
                put("user_input", userInput)
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