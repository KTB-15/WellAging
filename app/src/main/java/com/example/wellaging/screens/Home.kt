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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wellaging.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavController) {
    var fontSizeAdjustment by remember { mutableStateOf(0f) }

    Scaffold(
        topBar = {
            Column {
                TopAppBarWithFontControl(
                    onFontSizeIncrease = {
                        if (fontSizeAdjustment < 12f) {
                            fontSizeAdjustment += 4f
                        }
                    },
                    onFontSizeDecrease = {
                        if (fontSizeAdjustment > -8f) {
                            fontSizeAdjustment -= 4f
                        }
                    }
                )

                Image(
                    painter = painterResource(id = R.drawable.images),
                    contentDescription = "Ad Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
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
            SavingsProgress(fontSizeAdjustment)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithFontControl(
    onFontSizeIncrease: () -> Unit,
    onFontSizeDecrease: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                "WellAging",
                fontSize = 24.sp,
                color = Color.White
            )
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "가",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier
                        .clickable { onFontSizeDecrease() }
                        .padding(horizontal = 8.dp)
                )
                Text(
                    text = "가",
                    fontSize = 24.sp,
                    color = Color.White,
                    modifier = Modifier
                        .clickable { onFontSizeIncrease() }
                        .padding(horizontal = 8.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(red = 255, green = 65, blue = 145)
        )
    )
}

@Composable
fun SavingsProgress(fontSizeAdjustment: Float) {
    val totalSteps = 10000f
    val currentSteps = 1876f
    val progressPercentage = currentSteps / totalSteps
    val sections = 4

    val stepTextSize = (28f + fontSizeAdjustment).sp
    val labelTextSize = (20f + fontSizeAdjustment).sp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${currentSteps.toInt()} 걸음",
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