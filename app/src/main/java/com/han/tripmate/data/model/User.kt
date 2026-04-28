package com.han.tripmate.data.model

enum class UserRole {
    USER,   // 일반
    GUIDE   // 가이드
}

data class User(
    val id : String = "",
    val email : String = "",
    val nickname : String = "",
    val isVerified : Boolean = false,
    val rating : Double = 0.0,
    val currentRole : UserRole = UserRole.USER,
    val travelStyles: List<String> = emptyList(),
)


