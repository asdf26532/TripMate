package com.han.tripmate.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(planViewModel: PlanViewModel = viewModel()) {

    var showDialog by remember { mutableStateOf(false) }

    var selectedPlanIdForPhoto by remember { mutableStateOf<String?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedPlanIdForPhoto?.let { id ->
                planViewModel.uploadPlanImage(id, it)
            }
        }
    }

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
                    PlanItem(
                        plan = plan,
                        onAddPhotoClick = {
                            selectedPlanIdForPhoto = plan.id
                            galleryLauncher.launch("image/*")
                        }
                    )
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
fun PlanItem(
    plan: Plan,
    onAddPhotoClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // 상단 정보 (시간, 제목, 체크박스)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = plan.time, fontSize = 12.sp, color = Color.Gray)
                    Text(text = plan.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = plan.location, fontSize = 14.sp, color = Color.DarkGray)
                }
                // 일정 완료 체크박스
                Checkbox(
                    checked = plan.isCompleted,
                    onCheckedChange = { /* ViewModel에서 상태 변경 로직 호출 */ }
                )
            }

            // 인증샷 목록 (사진이 있을 때만 표시)
            if (plan.imageUrls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "여행 기록", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(plan.imageUrls) { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = "인증샷",
                            modifier = Modifier
                                .size(100.dp) // 사진 크기 조절
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // 하단 액션
            Spacer(modifier = Modifier.height(8.dp))
            Divider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))

            TextButton(
                onClick = onAddPhotoClick,
                modifier = Modifier.align(Alignment.End),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "인증샷 추가", fontSize = 13.sp)
            }
        }
    }
}