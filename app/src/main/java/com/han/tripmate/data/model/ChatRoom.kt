package com.han.tripmate.data.model

import java.text.SimpleDateFormat
import java.util.*

data class ChatRoom(
    val id: String = "",
    val otherUserId: String = "",
    val otherNickname: String = "",
    val lastMessage: String = "",
    val lastTimestamp: String = "",
    val profileImageUrl: String = "",
    val participants: List<String> = emptyList()
){
    fun getFormattedLastTime(): String {
        return try {
            if (lastTimestamp.isEmpty()) return ""
            val date = Date(lastTimestamp.toLong())
            val sdf = SimpleDateFormat("a h:mm", Locale.KOREA)
            sdf.format(date)
        } catch (e: Exception) {
            lastTimestamp
        }
    }
}
