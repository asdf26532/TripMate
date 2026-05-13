package com.han.tripmate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                        Text(
                            "₩ ${String.format("%,d", totalAllExpense)}",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            // 여행별 지출 리스트
            items(plans) { plan ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(plan.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(plan.location, fontSize = 12.sp, color = Color.Gray)
                    }
                    Text(
                        "₩ ${String.format("%,d", plan.totalExpense)}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE91E63)
                    )
                }
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
            }
        }
    }
}