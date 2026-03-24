package com.han.tripmate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.han.tripmate.data.model.Plan
import com.han.tripmate.ui.viewmodel.PlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(planViewModel: PlanViewModel = viewModel()) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("내 일정", fontWeight = FontWeight.Bold) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* 일정 추가 다이얼로그 띄우기 */ }) {
                Icon(Icons.Default.Add, contentDescription = "추가")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(planViewModel.plans) { plan ->
                PlanItem(plan = plan)
            }
        }
    }
}

@Composable
fun PlanItem(plan: Plan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = plan.time, fontSize = 12.sp, color = Color.Gray)
                Text(text = plan.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = plan.location, fontSize = 14.sp, color = Color.DarkGray)
            }
            // 상태 표시 (체크박스 등 나중에 추가)
            Checkbox(checked = plan.isCompleted, onCheckedChange = {})
        }
    }
}