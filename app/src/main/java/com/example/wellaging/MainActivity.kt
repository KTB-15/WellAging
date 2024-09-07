package com.example.wellaging

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

// ViewModel to manage font size
class FontSizeViewModel : ViewModel() {
    private val minFontSize = 8f
    private val maxFontSize = 20f
    private val fontSizeStep = 2f
    var fontSizeAdjustment = mutableStateOf(16f) // Default font size
    fun increaseFontSize() {
        fontSizeAdjustment.value = (fontSizeAdjustment.value + fontSizeStep).coerceAtMost(maxFontSize)
    }

    fun decreaseFontSize() {
        fontSizeAdjustment.value = (fontSizeAdjustment.value - fontSizeStep).coerceAtLeast(minFontSize)
    }
}

class MainActivity : ComponentActivity() {
    private val ACTIVITY_RECOGNITION_REQUEST_CODE = 100

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 권한 확인 및 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                ACTIVITY_RECOGNITION_REQUEST_CODE)
        }

        // Start the step counter service
        startService(Intent(this, StepCounterService::class.java))

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Obtain the ViewModel
                    val fontSizeViewModel: FontSizeViewModel = viewModel()

                    Column {
                        // Top bar with font control
                        TopAppBarWithFontControl(
                            onFontSizeIncrease = {
                                fontSizeViewModel.increaseFontSize()
                            },
                            onFontSizeDecrease = {
                                fontSizeViewModel.decreaseFontSize()
                            }
                        )

                        // Bottom navigation bar with screen switcher
                        BottomNavigationBar(fontSizeViewModel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithFontControl(
    onFontSizeIncrease: () -> Unit,
    onFontSizeDecrease: () -> Unit
) {
    Column {
        TopAppBar(
            title = {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        "WellAging",
                        fontSize = 24.sp,
                        color = Color(red = 255, green = 65, blue = 145),
                        textAlign = TextAlign.Left
                    )
                }
            },
            actions = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = "가",
                        fontSize = 16.sp,
                        color = Color(red = 255, green = 65, blue = 145),
                        modifier = Modifier
                            .clickable { onFontSizeDecrease() }
                            .padding(horizontal = 8.dp)
                    )
                    Text(
                        text = "가",
                        fontSize = 24.sp,
                        color = Color(red = 255, green = 65, blue = 145),
                        modifier = Modifier
                            .clickable { onFontSizeIncrease() }
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            ),
            modifier = Modifier.height(60.dp)
        )
        Divider(color = Color.LightGray, thickness = 0.5.dp)
    }
}