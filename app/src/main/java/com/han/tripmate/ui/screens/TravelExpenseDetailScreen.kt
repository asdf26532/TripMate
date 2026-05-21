package com.han.tripmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.han.tripmate.ui.viewmodel.PlanViewModel
import com.han.tripmate.ui.theme.CategoryStyle
import com.han.tripmate.ui.theme.MainBlue

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

    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val availableCategories = remember(itineraries) {
        itineraries.filter { it.cost > 0 }
            .map { it.category.ifBlank { "기타" }.trim() }
            .distinct()
    }

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
                Column(modifier = Modifier.padding(top = 8.dp)) {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "이 여행의 총 지출",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "₩ ${String.format("%,d", totalExpense)}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }


                    val maxExpenseItinerary = remember(itineraries) {
                        itineraries.filter { it.cost > 0 }.maxByOrNull { it.cost }
                    }

                    maxExpenseItinerary?.let { maxItem ->
                        val style = CategoryStyle.fromCategory(maxItem.category)

                        Spacer(modifier = Modifier.height(10.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.06f)),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(style.color.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🚨", fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "최대 지출 항목",
                                            fontSize = 11.sp,
                                            color = Color.Gray,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = maxItem.title,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.DarkGray
                                        )
                                    }
                                }
                                Text(
                                    text = "₩ ${String.format("%,d", maxItem.cost)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Red.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }


            item {
                Text("카테고리별 지출", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            if (categoryExpenses.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.05f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("📊", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "아직 등록된 소비 지출이 없습니다.",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "일정에 비용을 추가하면 통계가 계산됩니다.",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            } else {
                items(categoryExpenses) { (category, cost) ->
                    val ratio = if (totalExpense > 0) cost.toFloat() / totalExpense else 0f
                    val style = CategoryStyle.fromCategory(category)

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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = style.icon,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = category,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = "${String.format("%,d", cost)}원 (${(ratio * 100).toInt()}%)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = style.color
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { ratio },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            color = style.color,
                            trackColor = style.color.copy(alpha = 0.1f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            if (availableCategories.isNotEmpty()) {
                item {
                    Column {
                        Text("내역 필터", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // 전체보기 칩
                            item {
                                FilterChip(
                                    selected = selectedCategory == null,
                                    onClick = { selectedCategory = null },
                                    label = { Text("전체") }
                                )
                            }

                            // 개별 카테고리 칩 목록
                            items(availableCategories) { category ->
                                val style = CategoryStyle.fromCategory(category)
                                val isSelected = selectedCategory == elegantTrim(category)

                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedCategory = if (isSelected) null else elegantTrim(category)
                                    },
                                    label = { Text("${style.icon} $category") }
                                )
                            }
                        }
                    }
                }
            }

            item {
                val headerText = if (selectedCategory == null) "상세 지출 내역" else "[$selectedCategory] 지출 내역"
                Text(headerText, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            }


            val filteredItineraries = itineraries.filter { itinerary ->
                val isCostValid = itinerary.cost > 0
                val matchesCategory = selectedCategory == null || itinerary.category.ifBlank { "기타" }.trim() == selectedCategory
                isCostValid && matchesCategory
            }

            if (filteredItineraries.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🔍", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "선택하신 카테고리의 내역이 없습니다.",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))


                        TextButton(
                            onClick = { selectedCategory = null },
                            colors = ButtonDefaults.textButtonColors(contentColor = MainBlue)
                        ) {
                            Text("전체 내역 보기", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                items(filteredItineraries) { itinerary ->
                    val style = CategoryStyle.fromCategory(itinerary.category)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(style.color.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(style.icon, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(itinerary.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                Text(itinerary.category.ifBlank { "기타" }, fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                        Text(
                            "₩ ${String.format("%,d", itinerary.cost)}",
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.2f))
                }
            }
        }
    }
}

private fun elegantTrim(value: String): String = value.trim()