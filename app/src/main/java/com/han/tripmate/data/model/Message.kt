package com.han.tripmate.data.model

data class Message(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val imageUrl: String? = null,
    val timestamp: String = "",
    val chatRoomId: String = ""
) {
    constructor() : this("", "", "", null, "","")
}