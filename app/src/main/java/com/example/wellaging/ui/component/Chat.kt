package com.example.wellaging.ui.component

import android.os.AsyncTask
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wellaging.ui.theme.Purple40
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

class GptApiTask(private val onComplete: (String) -> Unit) : AsyncTask<String, Void, String>() {
    override fun doInBackground(vararg params: String): String {
        val message = params[0]
        val encodedMessage = URLEncoder.encode(message, "UTF-8")
        val urlString = "http://aws.lambda.gpt.com?prompt=$encodedMessage"
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        return try {
            val inputStream = connection.inputStream
            val response = inputStream.bufferedReader().use { it.readText() }
            inputStream.close()
            response
        } catch (e: Exception) {
            "죄송합니다. 응답을 받아오는 데 문제가 발생했습니다."
        } finally {
            connection.disconnect()
        }
    }

    override fun onPostExecute(result: String) {
        onComplete(result)
    }
}