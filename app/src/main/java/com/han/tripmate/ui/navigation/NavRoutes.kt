package com.han.tripmate.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

object Routes {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val MAIN = "main"
    const val DETAIL = "detail/{serviceId}"
}
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "홈", Icons.Default.Home)
    object Chatting : BottomNavItem("chatting", "채팅", Icons.Default.Forum)
    object Plan : BottomNavItem("plan", "내 일정", Icons.Default.DateRange)
    object Settings : BottomNavItem("settings", "설정", Icons.Default.Settings)

    companion object {
        val items = listOf(Home, Chatting, Plan, Settings)
    }
}
