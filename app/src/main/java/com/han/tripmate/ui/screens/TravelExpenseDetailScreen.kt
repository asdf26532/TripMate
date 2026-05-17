package com.han.tripmate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun TravelExpenseDetailScreen(
    planId: String,
    planTitle: String,
    viewModel: PlanViewModel,
    onBack: () -> Unit
) {

    LaunchedEffect(planId) {
        viewModel.loadItineraries(planId)
    }

    val itineraries by viewModel.itineraryList.collectAsState()
    val totalExpense = itineraries.sumOf { it.cost }


    val categoryExpenses = itineraries.groupBy { it.category.ifBlank { "기타" } }
        .mapValues { entry -> entry.value.sumOf { it.cost } }
        .toList()
        .sortedByDescending { it.second }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(planTitle, fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("이 여행의 총 지출", fontSize = 13.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                        Text(
                            "₩ ${String.format("%,d", totalExpense)}",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }


            item {
                Text("카테고리별 지출", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            if (categoryExpenses.isEmpty()) {
                item {
                    Text("등록된 지출 기록이 없습니다.", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                items(categoryExpenses) { (category, cost) ->
                    val ratio = if (totalExpense > 0) cost.toFloat() / totalExpense else 0f
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(category, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text("${String.format("%,d", cost)}원 (${(ratio * 100).toInt()}%)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { ratio },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            color = MainBlue,
                            trackColor = Color.LightGray.copy(alpha = 0.2f)
                        )
                    }
                }
            }


            item {
                Text("상세 지출 내역", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            }

            items(itineraries.filter { it.cost > 0 }) { itinerary ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(itinerary.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text(itinerary.category.ifBlank { "기타" }, fontSize = 12.sp, color = Color.Gray)
                    }
                    Text("₩ ${String.format("%,d", itinerary.cost)}", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                }
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.2f))
            }
        }
    }
}