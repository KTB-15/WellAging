package com.example.wellaging.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                if (inputText.isNotEmpty()) {
                    onChatSubmit(inputText)
                    inputText = ""
                }
            }) {
                Text("등록")
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
        Text(if (isListening) "말하기 종료" else "말하기")
    }
}