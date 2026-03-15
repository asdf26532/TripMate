package com.han.tripmate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.han.tripmate.ui.screens.ChatScreen
import com.han.tripmate.ui.screens.DetailScreen
import com.han.tripmate.ui.screens.LoginScreen
import com.han.tripmate.ui.screens.MainScreen
import com.han.tripmate.ui.screens.SignUpScreen
import com.han.tripmate.ui.viewmodel.AuthViewModel
import com.han.tripmate.ui.viewmodel.TravelViewModel

@Composable
fun TripMateNavGraph(navController: NavHostController) {

    val authViewModel : AuthViewModel = viewModel()
    val travelViewModel: TravelViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN // 시작 화면 로그인
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginClick = { email, password ->
                    authViewModel.login(email)
                    navController.navigate(Routes.MAIN) {
                    popUpTo(Routes.LOGIN) { inclusive = true } // 로그인 성공 시 백스택을 비워 뒤로가기 방지
                }
            },
                onSignUpClick = {
                    navController.navigate(Routes.SIGNUP)
                }
            )
        }

        composable(Routes.SIGNUP) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
            DetailScreen(
                serviceId = serviceId,
                travelViewModel = travelViewModel,
                onBack = { navController.popBackStack() },
                onChatClick = { guideId ->
                    navController.navigate("chat_screen/$guideId")
                }
            )
        }

        composable(
            route = Routes.CHAT_ROOM,
            arguments = listOf(navArgument("guideId") { type = NavType.StringType })
        ) { backStackEntry ->
            val guideId = backStackEntry.arguments?.getString("guideId") ?: ""
            ChatScreen(guideId = guideId, onBack = { navController.popBackStack() })
        }

        composable(Routes.MAIN) {
            MainScreen(authViewModel = authViewModel, travelViewModel = travelViewModel, navController = navController)
        }
    }
}
