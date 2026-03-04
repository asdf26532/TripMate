package com.han.tripmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.han.tripmate.ui.theme.MainBlue
import com.han.tripmate.ui.theme.TripMateTheme

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //  로고 섹션
        Text(
            text = "TripMate",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = MainBlue
        )
        Text(
            text = "당신의 여행 파트너",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(64.dp))

        // 로그인 버튼
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MainBlue)
        ) {
            Text(text = "로그인", fontSize = 18.sp, color = Color.White)
        }
        // 회원가입 버튼
        Button(
            onClick = onSignUpClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(top = 12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MainBlue)
        ) {
            Text(text = "회원이 아니신가요? 회원가입", fontSize = 18.sp, color = Color.White)
        }

    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    TripMateTheme {
        LoginScreen(onLoginClick = {}, onSignUpClick = {})
    }
}