package com.han.tripmate.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.han.tripmate.ui.navigation.BottomNavItem

@Composable
fun MainScreen() {
    // 현재 어떤 탭이 선택되었는지 관리하는 상태 (rememberSaveable 사용 추천)
    var selectedIndex by remember { mutableIntStateOf(0) }

    //  하단 탭 메뉴 리스트
    val items = BottomNavItem.items

    // Scaffold: 상단바, 하단바 등 기본 레이아웃 뼈대
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
            contentAlignment = Alignment.Center
        ) {
            // 선택된 인덱스에 따라 다른 화면(컴포저블)을 호출
            when (selectedIndex) {
                0 -> HomeScreen()
                1 -> ChattingScreen()
                2 -> PlanScreen()
                3 -> SettingsScreen()
            }
        }
    }
}

// 임시 화면 (에러 방지용)
@Composable
fun HomeScreen() { Text("홈 화면") }

@Composable
fun ChattingScreen() { Text("채팅 목록") }

@Composable
fun PlanScreen() { Text("내 일정 목록") }

@Composable
fun SettingsScreen() { Text("설정 화면") }