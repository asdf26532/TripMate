package com.han.tripmate.data.model

import java.text.SimpleDateFormat
import java.util.*

data class Message(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val imageUrl: String? = null,
    val timestamp: String = "",
    val chatRoomId: String = ""
) {
    constructor() : this("", "", "", null, "","")

    fun getFormattedTime(): String {
        return try {
            val date = Date(timestamp.toLong())
            val sdf = SimpleDateFormat("a h:mm", Locale.KOREA)
            sdf.format(date)
        } catch (e: Exception) {
            ""
        }
    }
}
