package com.han.tripmate.data.model

import java.util.UUID

data class Plan(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val date: String = "",
    val day: Int = 1,
    val time: String,
    val location: String,
    val isCompleted: Boolean = false,
    val imageUrls: List<String> = emptyList(),
    val memo: String = "",
    val expense: Int = 0,
    val authorId: String = "",
    val order: Int = 0
)
