package com.han.tripmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.han.tripmate.data.TravelService
import com.han.tripmate.ui.theme.MainBlue
import com.han.tripmate.ui.viewmodel.TravelViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabsScreen(
    navController: NavHostController,
    travelViewModel: TravelViewModel
) {
    val scope = rememberCoroutineScope()

    val allServices by travelViewModel.services.collectAsState()

    var isShuffling by remember { mutableStateOf(false) }
    var rolledService by remember { mutableStateOf<TravelService?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TripMate 실험실 🧪", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color(0xFFF1F3F5), shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (isShuffling) {
                    Text(
                        text = rolledService?.title ?: "디코딩 중...",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MainBlue,
                        textAlign = TextAlign.Center
                    )
                } else if (rolledService != null) {
                    Text(
                        text = "🎉 당첨! 🎉\n\n${rolledService!!.title}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        text = "버튼을 누르면\n랜덤 추천이 시작됩니다!",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (allServices.isEmpty()) return@Button

                    scope.launch {
                        isShuffling = true

                        var speed = 50L
                        repeat(20) {
                            val randomIndex = Random.nextInt(allServices.size)
                            rolledService = allServices[randomIndex]
                            delay(speed)
                            speed += 15L
                        }

                        isShuffling = false
                    }
                },
                enabled = !isShuffling,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(if (isShuffling) "추천 가이드 검색 중..." else "가이드 룰렛 돌리기 🚀")
            }
        }
    }
}