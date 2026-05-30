package com.han.tripmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.han.tripmate.ui.theme.MainBlue
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelPlannerScreen(
    onBack: () -> Unit = {},
    onSavePlan: (Long, Long, List<List<String>>) -> Unit = { _, _, _ -> }
) {

    val datePickerState = rememberDateRangePickerState()
    var showDatePicker by remember { mutableStateOf(false) }


    val sdf = remember { SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()) }
    val dateDisplayString = if (datePickerState.selectedStartDateMillis != null && datePickerState.selectedEndDateMillis != null) {
        "${sdf.format(Date(datePickerState.selectedStartDateMillis!!))} ~ ${sdf.format(Date(datePickerState.selectedEndDateMillis!!))}"
    } else {
        "여행 기간을 선택해 주세요"
    }


    val totalDays = remember(datePickerState.selectedStartDateMillis, datePickerState.selectedEndDateMillis) {
        val start = datePickerState.selectedStartDateMillis
        val end = datePickerState.selectedEndDateMillis
        if (start != null && end != null) {
            val diff = end - start
            (diff / (1000 * 60 * 60 * 24)).toInt() + 1
        } else {
            0
        }
    }


    var selectedDayIndex by remember { mutableStateOf(0) }


    val dayPlans = remember(totalDays) {
        mutableStateListOf<List<String>>().apply {
            val days = if (totalDays == 0) 1 else totalDays
            for (i in 0 until days) {
                add(listOf("인천국제공항 출발", "현지 숙소 체크인 및 정돈", "근처 대표 맛집 탐방", "야경 명소 산책"))
            }
        }
    }


    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("확인", color = MainBlue, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("취소", color = Color.Gray)
                }
            }
        ) {
            DateRangePicker(
                state = datePickerState,
                title = { Text("여행 일정 선택", modifier = Modifier.padding(start = 16.dp, top = 16.dp), fontWeight = FontWeight.Bold) },
                headline = { Text("기간을 지정해 주세요", modifier = Modifier.padding(start = 16.dp)) },
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = MainBlue,
                    selectedDayContentColor = Color.White,
                    dayInSelectionRangeContainerColor = MainBlue.copy(alpha = 0.12f)
                )
            )
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("새 일정 짜기", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (totalDays > 0) {
                Surface(
                    tonalElevation = 8.dp,
                    color = Color.White,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Button(
                        onClick = {
                            onSavePlan(
                                datePickerState.selectedStartDateMillis ?: 0L,
                                datePickerState.selectedEndDateMillis ?: 0L,
                                dayPlans
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MainBlue)
                    ) {
                        Text("이 일정으로 저장하기", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FA))
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MainBlue,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("여행 일정 날짜 설정", fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = dateDisplayString,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (totalDays > 0) Color.Black else Color.LightGray
                        )
                    }
                }
            }


            if (totalDays > 0) {
                ScrollableTabRow(
                    selectedTabIndex = selectedDayIndex,
                    edgePadding = 16.dp,
                    containerColor = Color.Transparent,
                    divider = {},
                    indicator = {}
                ) {
                    for (i in 0 until totalDays) {
                        val isSelected = selectedDayIndex == i
                        Tab(
                            selected = isSelected,
                            onClick = { selectedDayIndex = i },
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) MainBlue else Color.White)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) MainBlue else Color.LightGray.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Day ${i + 1}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color.Gray
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val currentDayPlan = dayPlans.getOrNull(selectedDayIndex) ?: emptyList()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(20.dp)
                ) {
                    itemsIndexed(currentDayPlan) { index, placeName ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(28.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(MainBlue)
                                        .border(2.5.dp, Color.White, CircleShape)
                                )
                                if (index != currentDayPlan.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(68.dp)
                                            .background(MainBlue.copy(alpha = 0.2f))
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 14.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = Color.LightGray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = placeName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = "코스 ${index + 1} • 방문 예정",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.LightGray.copy(alpha = 0.6f),
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "상단 달력에서 여행 기간을 선택하시면\n일자별 타임라인 플래너가 자동 생성됩니다.",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, showSystemUi = true)
@Composable
fun TravelPlannerScreenPreview() {
    MaterialTheme {
        TravelPlannerScreen(
            onBack = {},
            onSavePlan = { _, _, _ -> }
        )
    }
}