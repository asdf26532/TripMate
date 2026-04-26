package com.han.tripmate.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.han.tripmate.ui.theme.MainBlue
import com.han.tripmate.ui.util.LoadingOverlay
import com.han.tripmate.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()

    // 에러 메시지
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
            authViewModel.clearError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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

            OutlinedTextField(
                value = authViewModel.email,
                onValueChange = { authViewModel.onEmailChanged(it) },
                label = { Text("이메일") },
                modifier = Modifier.fillMaxWidth(),
                isError = authViewModel.emailError != null,
                supportingText = {
                    authViewModel.emailError?.let { Text(it) }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = authViewModel.password,
                onValueChange = { authViewModel.onPasswordChanged(it) },
                label = { Text("비밀번호") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = authViewModel.passwordError != null,
                supportingText = {
                    authViewModel.passwordError?.let { Text(it) }
                },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { authViewModel.login() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MainBlue),
                enabled = authViewModel.isLoginValid && !isLoading
            ) {
                Text(text = "로그인", fontSize = 18.sp, color = Color.White)
            }

            // 회원가입 이동 버튼
            TextButton(
                onClick = onSignUpClick,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(text = "계정이 없으신가요? 회원가입", color = MainBlue)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                Text(" 또는 ", modifier = Modifier.padding(horizontal = 8.dp), color = Color.Gray, fontSize = 12.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = { /* 구글 연동 로직 */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("G ", fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Google 계정으로 로그인", color = Color.Black)
                }
            }
        }

        if (isLoading) {
            LoadingOverlay()
        }
    }
}
