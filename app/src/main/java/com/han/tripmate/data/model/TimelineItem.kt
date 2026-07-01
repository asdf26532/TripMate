package com.han.tripmate.data.model

data class TimelineItem(
    val id: Int,
    val time: String,
    val spotName: String,
    val description: String,
    val duration: String,
    val transport: String?,
    val tip: String?
)