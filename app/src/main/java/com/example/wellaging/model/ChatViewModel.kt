package com.example.wellaging.model

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel

// ChatViewModel.kt
class ChatViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    private val _recognizedText = mutableStateOf("")
    val recognizedText: State<String> = _recognizedText


    private val _permissionNeeded = mutableStateOf(false)
    val permissionNeeded: State<Boolean> = _permissionNeeded

    private var speechRecognizer: SpeechRecognizer? = null
    private val _isListening = mutableStateOf(false)
    val isListening: State<Boolean> = _isListening

    private val _permissionGranted = mutableStateOf(false)
    val permissionGranted: State<Boolean> = _permissionGranted

    init {
        initializeSpeechRecognizer()
    }

    private fun initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplication())
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    _recognizedText.value = matches[0]
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
    }

    fun checkAudioPermission() {
        val context = getApplication<Application>()
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            _permissionGranted.value = false
        } else {
            _permissionGranted.value = true
        }
    }

    fun startListening() {
        val context = getApplication<Application>()
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
            }
            speechRecognizer?.startListening(intent)
            _isListening.value = true
        } else {
            // Handle unavailable speech recognition
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _isListening.value = false
    }

    fun clearRecognizedText() {
        _recognizedText.value = ""
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
    }

    fun checkAndRequestAudioPermission() {
        val context = getApplication<Application>()
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            _permissionNeeded.value = true
        } else {
            _permissionGranted.value = true
        }
    }

    fun onPermissionResult(granted: Boolean) {
        _permissionGranted.value = granted
        _permissionNeeded.value = false
    }
}