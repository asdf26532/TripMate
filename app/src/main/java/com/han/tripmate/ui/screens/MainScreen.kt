package com.han.tripmate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.han.tripmate.data.model.UserRole
import com.han.tripmate.ui.navigation.BottomNavItem
import com.han.tripmate.ui.viewmodel.AuthViewModel
import com.han.tripmate.ui.viewmodel.PlanViewModel
import com.han.tripmate.ui.viewmodel.SettingsViewModel
import com.han.tripmate.ui.viewmodel.TravelViewModel

@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
    travelViewModel: TravelViewModel,
    planViewModel: PlanViewModel,
    settingsViewModel: SettingsViewModel = viewModel(),
    navController: NavHostController
) {
    val user by authViewModel.currentUser.collectAsState()
    var selectedIndex by remember { mutableIntStateOf(0) }
    val items = BottomNavItem.items

    MaterialTheme(
        colorScheme = if (settingsViewModel.isDarkMode) darkColorScheme() else lightColorScheme()
    ) {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopStart
            ) {
                when (selectedIndex) {
                    0 -> {
                        HomeScreen(
                            authViewModel = authViewModel,
                            travelViewModel = travelViewModel,
                            navController = navController
                        )
                    }
                    1 -> ChatPreviewScreen(navController = navController)
                    2 -> {
                        PlanScreen(
                            planViewModel = planViewModel,
                            onNavigateToMap = { planId ->
                                navController.navigate("plan_map/$planId")
                            }
                        )
                    }
                    3 -> {
                        SettingsScreen(
                            viewModel = settingsViewModel,
                            onNavigateToLogin = {
                                navController.navigate("login") {
                                    popUpTo("main") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}


