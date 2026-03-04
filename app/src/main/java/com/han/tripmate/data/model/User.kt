package com.han.tripmate.data.model

data class User(
    val id : String = "",
    val email : String = "",
    val nickname : String = "",
    val isVerified : Boolean = false,
    val rating : Double = 0.0,
    val currentRole : UserRole = UserRole.USER
)

enum class UserRole {
    USER,   // 일반
    GUIDE   // 가이드
}

