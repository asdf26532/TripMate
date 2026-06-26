package com.han.tripmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
    var hasRolled by remember { mutableStateOf(false) }

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
                .background(Color(0xFFF8F9FA))
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "연구 과제 #01. 가이드 랜덤 디코더",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MainBlue
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "어디로 떠나야 할지 선택 장애가 찾아온 여행자를 위한 복불복 추천 엔진입니다. 룰렛을 가동해 우연이 선물하는 뜻밖의 로컬 가이드를 만나보세요!",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.2f))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .clickable(enabled = !isShuffling && rolledService != null) {
                        rolledService?.let { service ->
                            navController.navigate("detail/${service.id}")
                        }
                    },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isShuffling) Color(0xFF111827) else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                if (isShuffling) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF00F5FF), strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "AI 가이드 매칭 매트릭스 디코딩 중...",
                            color = Color(0xFF00F5FF),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = rolledService?.title ?: "",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                } else if (hasRolled && rolledService != null) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = rolledService?.images?.firstOrNull(),
                            contentDescription = "추천 가이드 썸네일",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = rolledService?.title ?: "",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${rolledService?.price}원",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MainBlue
                                )
                                Surface(
                                    color = MainBlue.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "👉 터치하여 상세화면 이동",
                                        color = MainBlue,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🎰", fontSize = 50.sp)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "오늘 어디 갈지 운명에 맡겨봐요.\n아래 룰렛을 당겨보세요!",
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )
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