package com.han.tripmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.han.tripmate.data.model.Plan
import com.han.tripmate.ui.theme.MainBlue
import com.han.tripmate.ui.viewmodel.AuthViewModel
import com.han.tripmate.ui.viewmodel.HomeViewModel
import com.han.tripmate.ui.viewmodel.PlanViewModel

@Composable
fun HomeScreenTest(
    authViewModel: AuthViewModel,
    planViewModel: PlanViewModel,
    homeViewModel: HomeViewModel = viewModel(),
    onAddPlanClick: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val plans by planViewModel.plans.collectAsState()
    val upcomingPlan by homeViewModel.upcomingPlan.collectAsState()
    val dDay by homeViewModel.dDay.collectAsState()

    LaunchedEffect(plans) {
        homeViewModel.calculateUpcomingPlan(plans)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Text(
            text = "${currentUser?.nickname ?: "여행자"}님,\n어디로 떠나볼까요?",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 34.sp,
            color = Color(0xFF1A1A1A)
        )

        Row(modifier = Modifier.padding(top = 8.dp)) {
            currentUser?.travelStyles?.take(3)?.forEach { style ->
                Text(
                    text = "#$style ",
                    fontSize = 14.sp,
                    color = MainBlue,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(listOf(MainBlue, Color(0xFF64B5F6)))
                    )
                    .padding(24.dp)
            ) {
                if (upcomingPlan != null) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FlightTakeoff, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("다가오는 여행", color = Color.White.copy(0.9f), fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = upcomingPlan!!.title,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (dDay == 0L) "오늘 떠나요! ✈️" else "출발까지 ${dDay}일 남았어요",
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("아직 예정된 여행이 없어요", color = Color.White, fontWeight = FontWeight.Bold)
                        Button(
                            onClick = onAddPlanClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            modifier = Modifier.padding(top = 12.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = MainBlue)
                            Text("첫 일정 만들기", color = MainBlue)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("추천 여행 테마", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ThemeCard("제주도 맛집", "🥘", Modifier.weight(1f))
            ThemeCard("일본 온천", "♨️", Modifier.weight(1f))
        }
    }
}

@Composable
fun ThemeCard(title: String, emoji: String, modifier: Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 30.sp)
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun TravelTipSection(plan: Plan) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("💡", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "${plan.location} 여행 꿀팁",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MainBlue
                )
                Text(
                    text = "이 지역은 오후에 비 소식이 있을 수 있어요. 우산을 챙기세요!",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}