package com.example.wellaging.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.wellaging.FontSizeViewModel
import com.example.wellaging.MyApplication
import com.example.wellaging.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController,
    fontSizeViewModel: FontSizeViewModel
) {

    Scaffold(
        topBar = {
            Column {
                Image(
                    painter = painterResource(id = R.drawable.images),
                    contentDescription = "Ad Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(15.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            SavingsProgress(fontSizeViewModel)
        }
    }
}

@Composable
fun SavingsProgress(fontSizeViewModel: FontSizeViewModel) {
    val dataStore = (LocalContext.current.applicationContext as MyApplication).dataStore
    val steps = dataStore.data
        .map { preferences -> preferences[intPreferencesKey("steps")] ?: 0 }
        .collectAsState(initial = 0)
    val totalSteps = 10000f
    val progressPercentage = ((steps.value-107000) / totalSteps).coerceIn(0f, 1f)
    val sections = 4

    val stepTextSize = (28f + fontSizeViewModel.fontSizeAdjustment.value).sp
    val labelTextSize = (20f + fontSizeViewModel.fontSizeAdjustment.value).sp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${steps.value-107000} 걸음",
            fontSize = stepTextSize,
            fontWeight = FontWeight.Bold,
            color = Color(red=255, green=65, blue=145)
        )

        Spacer(modifier = Modifier.height(16.dp))

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            val barWidth = constraints.maxWidth.toFloat()
            val iconOffsetX = (barWidth * progressPercentage).coerceIn(0f, barWidth)

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(24.dp)) {
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = size.height
                    )

                    drawLine(
                        color = Color(red=255, green=65, blue=145),
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width * progressPercentage, size.height / 2),
                        strokeWidth = size.height
                    )

                    val sectionWidth = size.width / sections
                    for (i in 1 until sections) {
                        drawLine(
                            color = Color.Black,
                            start = Offset(sectionWidth * i, 0f),
                            end = Offset(sectionWidth * i, size.height),
                            strokeWidth = 3f
                        )
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.walkingman),
                    contentDescription = "Running Person",
                    modifier = Modifier
                        .offset(x = with(LocalDensity.current) { iconOffsetX.toDp() - 12.dp }, y = (-16).dp)
                        .size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "0", fontSize = labelTextSize)
            Text(text = "2500", fontSize = labelTextSize)
            Text(text = "5000", fontSize = labelTextSize)
            Text(text = "7500", fontSize = labelTextSize)
            Text(text = "10000", fontSize = labelTextSize)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}