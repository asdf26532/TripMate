package com.han.tripmate.data.model

import java.util.UUID

data class Plan(
    val id: String = UUID.randomUUID().toString(),
    val title: String,        // 일정 제목
    val date: String,         // 날짜
    val time: String,         // 시간
    val location: String,     // 장소 상세
    val isCompleted: Boolean = false // 완료 여부
)
