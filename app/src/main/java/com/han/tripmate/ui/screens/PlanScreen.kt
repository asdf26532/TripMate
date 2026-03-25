package com.han.tripmate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.han.tripmate.data.model.Plan
import com.han.tripmate.ui.viewmodel.PlanViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(planViewModel: PlanViewModel = viewModel()) {

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("내 일정", fontWeight = FontWeight.Bold) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "추가", tint = Color.White)
            }
        }
    ) { padding ->
        if (planViewModel.plans.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("등록된 일정이 없습니다.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(planViewModel.plans) { plan ->
                    PlanItem(plan = plan)
                }
            }
        }

        if (showDialog) {
            AddPlanDialog(
                onDismiss = { showDialog = false },
                onConfirm = { title, time, location ->
                    planViewModel.addPlan(Plan(title = title, time = time, location = location, date = "2024-03-25"))
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AddPlanDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("새 일정 추가", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("일정 제목") })
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("시간 (예: 14:00)") })
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("장소") })
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (title.isNotBlank()) onConfirm(title, time, location) },
                enabled = title.isNotBlank()
            ) { Text("추가") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        }
    )
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