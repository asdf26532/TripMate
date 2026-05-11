package com.han.tripmate.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.han.tripmate.ui.util.EmptyStateView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.han.tripmate.data.model.Plan
import com.han.tripmate.ui.theme.MainBlue
import com.han.tripmate.ui.util.LoadingOverlay
import com.han.tripmate.ui.util.UiState
import com.han.tripmate.ui.viewmodel.PlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    planViewModel: PlanViewModel = viewModel(),
    onNavigateToMap: (String) -> Unit,
    navController: NavHostController
) {
    val groupedPlans by planViewModel.groupedPlans.collectAsState()
    val totalExpense by planViewModel.totalExpense.collectAsState()
    val uiState by planViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var selectedDay by remember { mutableIntStateOf(1) }

    val dayExpense = remember(groupedPlans, selectedDay) {
        groupedPlans[selectedDay]?.sumOf { it.expense } ?: 0
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedPlanIdForPhoto by remember { mutableStateOf<String?>(null) }
    var selectedPlanForEdit by remember { mutableStateOf<Plan?>(null) }

    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> {
                Toast.makeText(context, (uiState as UiState.Success).data, Toast.LENGTH_SHORT).show()
                planViewModel.resetUiState()
            }
            is UiState.Error -> {
                Toast.makeText(context, (uiState as UiState.Error).message, Toast.LENGTH_SHORT).show()
                planViewModel.resetUiState()
            }
            else -> {}
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedPlanIdForPhoto?.let { id ->
                planViewModel.uploadPlanImage(id, it)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("내 일정", fontWeight = FontWeight.Bold) }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "추가", tint = Color.White)
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {

                PlanSummaryCard(totalExpense = totalExpense, dayExpense = dayExpense, selectedDay = selectedDay)

                // 2. [신규] 날짜 선택 탭
                if (groupedPlans.isNotEmpty()) {
                    ScrollableTabRow(
                        selectedTabIndex = (groupedPlans.keys.sorted().indexOf(selectedDay)).coerceAtLeast(0),
                        edgePadding = 16.dp,
                        containerColor = Color.White,
                        contentColor = MainBlue,
                        divider = {}
                    ) {
                        groupedPlans.keys.sorted().forEach { day ->
                            Tab(
                                selected = selectedDay == day,
                                onClick = { selectedDay = day },
                                text = { Text("${day}일차", fontWeight = FontWeight.Bold) }
                            )
                        }
                    }
                }

                val currentPlans = groupedPlans[selectedDay] ?: emptyList()

                if (currentPlans.isEmpty()) {
                    EmptyStateView(
                        icon = Icons.Default.AddLocation,
                        message = "${selectedDay}일차 일정이 없습니다.\n새로운 여행지를 추가해 보세요!"
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(currentPlans) { plan ->
                            PlanItem(
                                plan = plan,
                                onAddPhotoClick = {
                                    selectedPlanIdForPhoto = plan.id
                                    galleryLauncher.launch("image/*")
                                },
                                onEditClick = {
                                    selectedPlanForEdit = plan
                                    showEditDialog = true
                                },
                                onItemClick = { navController.navigate("plan_detail/${plan.id}/${plan.title}") }
                            )
                        }
                    }
                }
            }
        }

        if (uiState is UiState.Loading) {
            LoadingOverlay()
        }

        if (showAddDialog) {
            AddPlanDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { title, time, location ->
                    planViewModel.addPlan(Plan(title = title, time = time, location = location, day = selectedDay))
                    showAddDialog = false
                }
            )
        }

        if (showEditDialog && selectedPlanForEdit != null) {
            EditPlanDetailsDialog(
                plan = selectedPlanForEdit!!,
                onDismiss = { showEditDialog = false },
                onConfirm = { memo, expense ->
                    planViewModel.updatePlanDetails(selectedPlanForEdit!!.id, memo, expense)
                    showEditDialog = false
                }
            )
        }
    }
}

@Composable
fun PlanItem(
    plan: Plan,
    onAddPhotoClick: () -> Unit,
    onEditClick: () -> Unit,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onItemClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = plan.time, fontSize = 12.sp, color = Color.Gray)
                    Text(text = plan.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = plan.location, fontSize = 14.sp, color = Color.DarkGray)

                    if (plan.memo.isNotBlank() || plan.expense > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (plan.memo.isNotBlank()) {
                                Text(
                                    text = "📝 ${plan.memo}",
                                    fontSize = 12.sp,
                                    color = Color.Blue,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                            if (plan.expense > 0) {
                                Text(
                                    text = "💰 ${plan.expense}원",
                                    fontSize = 12.sp,
                                    color = Color(0xFFE91E63)
                                )
                            }
                        }
                    }
                }
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.EditNote, contentDescription = "기록", tint = Color.Gray)
                }
            }

            // 인증샷 목록 (사진이 있을 때만 표시)
            if (plan.imageUrls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(plan.imageUrls) { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = "인증샷",
                            modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))

            TextButton(
                onClick = onAddPhotoClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "인증샷 추가", fontSize = 13.sp)
            }
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
fun EditPlanDetailsDialog(
    plan: Plan,
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    var memo by remember { mutableStateOf(plan.memo) }
    var expense by remember { mutableStateOf(plan.expense.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("여행 기록하기", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = memo,
                    onValueChange = { memo = it },
                    label = { Text("메모") },
                    placeholder = { Text("맛집 탐방 성공!") }
                )
                OutlinedTextField(
                    value = expense,
                    onValueChange = { if (it.all { c -> c.isDigit() }) expense = it },
                    label = { Text("지출 금액 (원)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(memo, expense.toIntOrNull() ?: 0) }) { Text("저장") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        }
    )
}

@Composable
fun PlanSummaryCard(totalExpense: Int, dayExpense: Int, selectedDay: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MainBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${selectedDay}일차 지출", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            Text(
                text = "₩ ${String.format("%,d", dayExpense)}",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "총 누적 지출: ₩ ${String.format("%,d", totalExpense)}",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
        }
    }
}