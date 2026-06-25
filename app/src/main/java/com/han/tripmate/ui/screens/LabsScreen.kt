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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (rolledService != null) {
                        AsyncImage(
                            model = rolledService?.images,
                            contentDescription = "가이드 이미지",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = rolledService?.title ?: "",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            // 가격 및 뱃지 행
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isShuffling) "가격 계산 중..." else "${rolledService?.price}원",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MainBlue
                                )

                                if (!isShuffling) {
                                    Surface(
                                        color = MainBlue.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "추천 가이드",
                                            color = MainBlue,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // 초기 상태 (아직 아무것도 안 뽑혔을 때)
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "어디로 떠날지 모르겠다면?\n아래 버튼을 눌러보세요! 🎰",
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                fontSize = 16.sp,
                                lineHeight = 24.sp
                            )
                        }
                    }
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