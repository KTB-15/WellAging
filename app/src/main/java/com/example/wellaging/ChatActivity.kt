package com.example.wellaging

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.wellaging.ui.theme.WellAgingTheme

class ChatActivity : ComponentActivity() {

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    private lateinit var speechRecognizer: SpeechRecognizer
    private var isListening by mutableStateOf(false)
    private var recognizedText by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 권한 설정
        checkAudioPermission()

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    recognizedText = matches[0]
                }
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}

        })

        setContent {
            WellAgingTheme {
                ChatScreen(
                    onStartListening = { startListening() },
                    onStopListening = {
                        stopListening()
                        Log.d("ChatActivity", "Recognized Text: $recognizedText")
                    },
                    recognizedText = recognizedText,
                    onChatSubmit = { message ->
                        Toast.makeText(this@ChatActivity, "채팅 등록됨: $message", Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            }
        }
    }

    private fun checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "권한 허용됨", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "권한이 거부되었습니다. 음성 인식을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun startListening() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
            }
            speechRecognizer.startListening(intent)
            isListening = true
        } else {
            Toast.makeText(this, "음성 인식이 불가능합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopListening() {
        speechRecognizer.stopListening()
        isListening = false
    }

    override fun onDestroy() {
        speechRecognizer.destroy()
        super.onDestroy()
    }
}

@Composable
fun ChatScreen(
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    recognizedText: String,
    onChatSubmit: (String) -> Unit
) {
    var inputText by remember { mutableStateOf(TextFieldValue("")) }
    var isListening by remember { mutableStateOf(false) }
    var hasRecognizedText by remember { mutableStateOf(false) }

    LaunchedEffect(recognizedText) {
        if (recognizedText.isNotEmpty() && !hasRecognizedText) {
            inputText = TextFieldValue(recognizedText)
            hasRecognizedText = true
        } else if (isListening && !hasRecognizedText) {
            inputText = TextFieldValue("아무 소리도 안들려요")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "안녕하세요~ 아침은 드셨나요?",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isListening) {
                    onStopListening()
                } else {
                    onStartListening()
                }
                isListening = !isListening
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isListening) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(if (isListening) "말하기 종료" else "말하기")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (recognizedText.isNotEmpty()) {
            inputText = TextFieldValue(recognizedText)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, MaterialTheme.colorScheme.primary)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                onChatSubmit(inputText.text)
                inputText = TextFieldValue("")
            }) {
                Text("등록")
            }
        }
    }
}