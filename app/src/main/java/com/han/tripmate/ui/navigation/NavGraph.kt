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
import com.han.tripmate.ui.screens.PlanDetailScreen
import com.han.tripmate.ui.screens.PlanMapScreen
import com.han.tripmate.ui.screens.SignUpScreen
import com.han.tripmate.ui.screens.TravelHistoryScreen
import com.han.tripmate.ui.viewmodel.AuthViewModel
import com.han.tripmate.ui.viewmodel.PlanViewModel
import com.han.tripmate.ui.viewmodel.TravelViewModel

@Composable
fun TripMateNavGraph(navController: NavHostController) {

    val authViewModel: AuthViewModel = viewModel()
    val travelViewModel: TravelViewModel = viewModel()
    val planViewModel: PlanViewModel = viewModel()

    // 상태 수집
    val currentUser by authViewModel.currentUser.collectAsState()
    val plans by planViewModel.plans.collectAsState()

    // 로그인 상태 감지 및 자동 화면 전환
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navController.navigate(Routes.MAIN) {

                popUpTo(Routes.LOGIN) { inclusive = true }
                popUpTo(Routes.SIGNUP) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        // 로그인 화면
        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {

                },
                onSignUpClick = {
                    navController.navigate(Routes.SIGNUP)
                }
            )
        }

        //  회원가입 화면
        composable(Routes.SIGNUP) {
            SignUpScreen(
                authViewModel = authViewModel,
                onSignUpSuccess = {

                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // 메인 화면 (홈, 일정, 채팅목록 등 포함)
        composable(Routes.MAIN) {
            MainScreen(
                authViewModel = authViewModel,
                travelViewModel = travelViewModel,
                planViewModel = planViewModel,
                navController = navController
            )
        }

        // 상세 화면 (여행 상품/가이드 정보)
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
                    navController.navigate("${Routes.CHAT_ROOM}/$guideId")
                }
            )
        }

        // 서비스 등록 화면
        composable(route = Routes.ADD_SERVICE) {
            AddServiceScreen(
                authViewModel = authViewModel,
                travelViewModel = travelViewModel,
                navController = navController
            )
        }

        // 채팅방 화면
        composable(
            route = Routes.CHAT_ROOM,
            arguments = listOf(navArgument("guideId") { type = NavType.StringType })
        ) { backStackEntry ->
            val guideId = backStackEntry.arguments?.getString("guideId") ?: ""
            ChatScreen(
                guideId = guideId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // 일정 지도 보기 화면
        composable(
            route = Routes.PLAN_MAP,
            arguments = listOf(navArgument("planId") { type = NavType.StringType })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: ""

            val selectedPlan = plans.find { it.id == planId }

            if (selectedPlan != null) {
                PlanMapScreen(
                    currentPlan = selectedPlan,
                    allPlans = plans,
                    viewModel = planViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = Routes.PLAN_DETAIL,
            arguments = listOf(
                navArgument("planId") { type = NavType.StringType },
                navArgument("planTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: ""
            val planTitle = backStackEntry.arguments?.getString("planTitle") ?: ""

            // 뷰모델 상태 관찰
            val itineraryList by planViewModel.itineraryList.collectAsState()

            LaunchedEffect(planId) {
                planViewModel.loadItineraries(planId)
            }

            PlanDetailScreen(
                planTitle = planTitle,
                planId = planId,
                itineraryList = itineraryList,
                planViewModel = planViewModel
            )
        }

        composable(Routes.TRAVEL_HISTORY) {
            TravelHistoryScreen(
                viewModel = planViewModel,
                onBack = { navController.popBackStack() }
            )
        }

    }
}
