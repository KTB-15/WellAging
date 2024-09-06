package com.example.wellaging

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.Modifier
import androidx.compose.material.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {

    private val sensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private val sensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            // 권한이 거부되었을 때의 처리
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {
                // Obtain the ViewModel

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column {
                            TopAppBarWithFontControl(
                                onFontSizeIncrease = {
                                    //fontSizeViewModel.fontSizeAdjustment.value += 2f
                                },
                                onFontSizeDecrease = {
                                    //fontSizeViewModel.fontSizeAdjustment.value -= 2f
                                }
                            )

                            BottomNavigationBar()
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
                        .fillMaxHeight() // Fill the height of the TopAppBar
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart // Align content to center
                ) {
                    Text(
                        "WellAging",
                        fontSize = 24.sp,
                        color = Color(red = 255, green = 65, blue = 145),
                        textAlign = TextAlign.Left // Center the text horizontally
                    )
                }
            },
            actions = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center, // Align horizontally to center
                    modifier = Modifier.fillMaxHeight() // Fill the height of the TopAppBar
                ) {
                    Text(
                        text = "가",
                        fontSize = 16.sp,
                        color = Color(red = 255, green = 65, blue = 145),
                        modifier = Modifier
                            .clickable { onFontSizeDecrease() }
                            .padding(horizontal = 8.dp)
                            //.padding(horizontal = 8.dp)
                    )
                    Text(
                        text = "가",
                        fontSize = 24.sp,
                        color = Color(red = 255, green = 65, blue = 145),
                        modifier = Modifier
                            .clickable { onFontSizeIncrease() }
                            //.padding(horizontal = 8.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            ),
            modifier = Modifier.height(60.dp) // Adjust the height of the TopAppBar
        )
        // Add a divider below the TopAppBar
        Divider(color = Color.LightGray, thickness = 0.5.dp)
    }
}