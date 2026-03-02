package com.han.tripmate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.han.tripmate.ui.screens.LoginScreen
import com.han.tripmate.ui.screens.MainScreen

@Composable
fun TripMateNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN // 시작 화면을 로그인
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(onLoginClick = {
                // 로그인 성공 시 메인으로 이동하는 로직
                navController.navigate(Routes.MAIN)
            })
        }
        composable(Routes.MAIN) {
            MainScreen()
        }
    }
}
