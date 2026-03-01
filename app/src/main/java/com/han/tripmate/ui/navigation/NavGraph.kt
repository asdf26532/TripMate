package com.han.tripmate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

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
            // 임시 텍스트
        }
        composable(Routes.MAIN) {
            //  메인 화면 컴포저블
        }
    }
}