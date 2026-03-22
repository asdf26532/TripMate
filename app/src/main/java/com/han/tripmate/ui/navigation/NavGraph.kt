package com.han.tripmate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.han.tripmate.ui.screens.AddServiceScreen
import com.han.tripmate.ui.screens.ChatPreviewScreen
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

    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navController.navigate(Routes.MAIN) {
                // 로그인이나 회원가입 화면을 백스택에서 완전히 제거
                popUpTo(Routes.LOGIN) { inclusive = true }
                popUpTo(Routes.SIGNUP) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN // 시작 화면 로그인
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginClick = { email, password ->
                    authViewModel.login(email, password)
            },
                onSignUpClick = {
                    navController.navigate(Routes.SIGNUP)
                }
            )
        }

        composable(Routes.SIGNUP) {
            SignUpScreen(
                onSignUpSuccess = { email, password, nickname ->
                    authViewModel.signUp(email, password, nickname)
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

        composable(route = Routes.ADD_SERVICE) {
            AddServiceScreen(
                authViewModel = authViewModel,
                travelViewModel = travelViewModel,
                navController = navController
            )
        }

        composable(BottomNavItem.Chatting.route) {
            ChatPreviewScreen(navController)
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
