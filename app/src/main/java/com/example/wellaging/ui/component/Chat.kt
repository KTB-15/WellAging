package com.example.wellaging.ui.component

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
import com.example.wellaging.ui.theme.Pink80
import com.example.wellaging.ui.theme.PinkTemp

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

@Composable
fun ChatList(messages: List<String>) {
    LazyColumn {
        items(messages) { message ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                ChatTextBox(message = message)
            }
        }
    }
}

@Composable
fun ChatTextBox(message: String) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = Pink80,
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
    onChatSubmit: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }

    LaunchedEffect(recognizedText) {
        if (recognizedText.isNotEmpty()) {
            inputText = recognizedText
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
                onValueChange = { inputText = it },
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
                    PinkTemp
                ),
                onClick = {
                    if (inputText.isNotEmpty()) {
                        onChatSubmit(inputText)
                        inputText = ""
                    }
                }
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