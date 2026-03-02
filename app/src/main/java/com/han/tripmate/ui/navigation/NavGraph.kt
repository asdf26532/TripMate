package com.han.tripmate.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.han.tripmate.ui.screens.LoginScreen

// 화면 경로 정의
object Routes {
    const val LOGIN = "login"
    const val MAIN = "main"
}

@Composable
fun TripMateNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN // 시작 화면을 로그인
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(onLoginClick = {
                // 로그인 성공 시 메인으로 이동하는 로직 (나중에 구현)
                navController.navigate(Routes.MAIN)
            })
        }
        composable(Routes.MAIN) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "메인 화면에 오신 것을 환영합니다!")
            }
        }
    }
}