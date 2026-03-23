package com.han.tripmate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.han.tripmate.data.model.UserRole
import com.han.tripmate.ui.navigation.BottomNavItem
import com.han.tripmate.ui.viewmodel.AuthViewModel
import com.han.tripmate.ui.viewmodel.TravelViewModel

@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
    travelViewModel: TravelViewModel,
    navController: NavHostController
) {
    val user by authViewModel.currentUser.collectAsState()
    var selectedIndex by remember { mutableIntStateOf(0) }
    val items = BottomNavItem.items

    Scaffold(
        bottomBar = {
            // Material3 디자인의 표준 하단 네비게이션 바
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp // 살짝 그림자 효과
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        label = { Text(text = item.title) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.outline,
                            unselectedTextColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        // 콘텐츠 영역
        // Scaffold가 제공하는 padding 값 적용(하단바에 가려짐 방지)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopStart
        ) {
            // 선택된 인덱스에 따라 다른 화면(컴포저블)을 호출
            when (selectedIndex) {
                0 -> {
                    if (user?.currentRole == UserRole.GUIDE) {
                        GuideDashboardScreen()
                    } else {
                        HomeScreen(
                            authViewModel = authViewModel,
                            travelViewModel = travelViewModel,
                            navController = navController
                        )
                    }
                }
                1 -> ChatPreviewScreen(navController = navController)
                2 -> PlanScreen()
                3 -> SettingsScreen(
                    authViewModel = authViewModel,
                    navController = navController
                )
            }
        }
    }
}

// 임시 화면
@Composable
fun GuideDashboardScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("👨‍✈️ 가이드 전용 대시보드", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("등록한 여행 상품 및 예약 현황을 관리하세요.")
        }
    }
}

@Composable
fun PlanScreen() { Text("내 일정 목록") }

