package com.han.tripmate.data.model

data class ChatRoom(
    val id: String = "",
    val otherUserId: String = "",
    val otherNickname: String = "",
    val lastMessage: String = "",
    val lastTime: String = "",
    val profileImageUrl: String = ""
)
