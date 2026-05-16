package com.han.tripmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.han.tripmate.ui.theme.MainBlue
import com.han.tripmate.ui.viewmodel.PlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelHistoryScreen(
    viewModel: PlanViewModel,
    onBack: () -> Unit
) {
    val plans by viewModel.plans.collectAsState()
    val totalAllExpense = plans.sumOf { it.totalExpense }

    val maxExpensePlan = plans.maxByOrNull { it.totalExpense }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("내 여행 내역", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 상단 총 지출 요약 카드
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MainBlue)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("총 여행 지출", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "₩ ${String.format("%,d", totalAllExpense)}",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            if (maxExpensePlan != null && maxExpensePlan.totalExpense > 0) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFE082)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFFF57F17))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("최대 지출 여행지", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                                Text(
                                    "${maxExpensePlan.title} (${String.format("%,d", maxExpensePlan.totalExpense)}원)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }

            // 리스트 헤더
            item {
                Text(
                    text = "여행별 리포트",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // 여행별 지출 리스트 & 게이지 바
            if (plans.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                        Text("아직 등록된 여행 내역이 없습니다.", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            } else {
                items(plans) { plan ->
                    val progress = if (totalAllExpense > 0) plan.totalExpense.toFloat() / totalAllExpense else 0f

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(plan.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(plan.location, fontSize = 12.sp, color = Color.Gray)
                            }
                            Text(
                                "₩ ${String.format("%,d", plan.totalExpense)}",
                                fontWeight = FontWeight.Bold,
                                color = if (plan.totalExpense == maxExpensePlan?.totalExpense) Color(0xFFE91E63) else Color.DarkGray,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = if (plan.totalExpense == maxExpensePlan?.totalExpense) Color(0xFFE91E63) else MainBlue,
                            trackColor = Color.LightGray.copy(alpha = 0.2f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.2f))
                    }
                }
            }
        }
    }
}