package com.example.wellaging

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

class ChatActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatScreen()
        }
    }

    @Composable
    fun ChatScreen() {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Welcome to Chat Screen!",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}