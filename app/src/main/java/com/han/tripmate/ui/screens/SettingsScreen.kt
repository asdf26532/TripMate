package com.han.tripmate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.han.tripmate.ui.viewmodel.AuthViewModel

@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    val user by authViewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 프로필 영역
        Surface(
            modifier = Modifier.size(80.dp).clip(CircleShape),
            color = Color.LightGray
        ) {
            // 프로필 이미지 처리
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = user?.nickname ?: "사용자", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = user?.email ?: "이메일 정보 없음", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider()

        // 가이드 모드
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "가이드 모드로 사용", fontWeight = FontWeight.Bold)
                Text(text = "내가 등록한 여행 서비스를 관리=", fontSize = 12.sp, color = Color.Gray)
            }
            Switch(
                checked = user?.currentRole == com.han.tripmate.data.model.UserRole.GUIDE,
                onCheckedChange = { isGuide ->
                    // 뷰모델에 역할 변경 요청
                    authViewModel.toggleRole(isGuide)
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // 로그아웃 버튼
        TextButton(onClick = {
            authViewModel.logout()
            navController.navigate("login") {
                popUpTo("main") { inclusive = true }
            }
        }, modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor= Color(0xFFF44336))
        ) {
            Text("로그아웃", color = Color.White)
        }
    }

}