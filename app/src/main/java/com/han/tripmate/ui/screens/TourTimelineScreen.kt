package com.han.tripmate.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.han.tripmate.data.model.TimelineItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourTimelineScreen(
    serviceTitle: String = "서울 역사 문화 탐방 투어",
    onBack: () -> Unit
) {
    // 테스트용
    val timelineList = remember {
        listOf(
            TimelineItem(1, "09:30 AM", "광화문 광장 집결", "가이드와 미팅 및 오늘 일정 브리핑을 진행합니다.", "30분", "도보 10분", "스타벅스 광화문점 앞에서 만나요!"),
            TimelineItem(2, "10:00 AM", "경복궁 해설 투어", "근정전과 경회루를 돌며 조선 왕실의 숨겨진 역사를 파헤칩니다.", "2시간", "차량 15분", "한복 착용 시 입장료가 면제되니 참고하세요."),
            TimelineItem(3, "12:15 PM", "인사동 쌈지길 금강산도 식후경", "가이드가 추천하는 숨은 보리밥 맛집에서 점심 식사를 합니다.", "1시간 30분", "도보 5분", "인사동 골목길은 길을 잃기 쉬우니 잘 따라오세요!"),
            TimelineItem(4, "02:00 PM", "북촌 한옥마을 산책", "전통 한옥의 아름다움을 느끼며 인생샷 스팟을 투어합니다.", "1시간", null, "실제 주민들이 거주하는 곳이니 조용히 관람하는 매너 필수!")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "$serviceTitle 일정", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            itemsIndexed(timelineList) { index, item ->
                TimelineRow(
                    item = item,
                    isLast = index == timelineList.lastIndex
                )
            }
        }
    }
}