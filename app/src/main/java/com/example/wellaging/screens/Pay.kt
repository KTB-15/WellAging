package com.example.wellaging.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wellaging.FontSizeViewModel
import com.example.wellaging.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pay(
    navController: NavController,
    fontSizeViewModel: FontSizeViewModel
) {
    var accountNumber by remember { mutableStateOf("") }
    var transferAmount by remember { mutableStateOf("") }

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
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 제목 텍스트
                Text(
                    text = "돈 꺼내기",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(red=255, green=65, blue=145), // 오렌지 색상
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // 금액 표시 텍스트
                Text(
                    text = "2,000원이 있으세요",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(red=255, green=65, blue=145),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 계좌 입력 필드
                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    label = { Text("은행 계좌를 입력해주세요") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(red = 255, green = 165, blue = 0),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                // 송금 금액 입력 필드
                OutlinedTextField(
                    value = transferAmount,
                    onValueChange = { transferAmount = it },
                    label = { Text("송금할 금액") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(red = 255, green = 165, blue = 0),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                // 송금 버튼
                Button(
                    onClick = { /* 송금 처리 로직 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(red=255, green=65, blue=145),
                        contentColor = Color.White
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = "돈 꺼내기", fontSize = 18.sp)
                }
            }
        }
    )
}