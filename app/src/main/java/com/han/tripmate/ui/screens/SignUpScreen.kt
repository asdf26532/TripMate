package com.han.tripmate.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.han.tripmate.ui.theme.MainBlue
import com.han.tripmate.ui.util.LoadingOverlay
import com.han.tripmate.ui.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel,
    onSignUpSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var isAgreed by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // 회원가입 성공 처리
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            onSignUpSuccess()
        }
    }

    // 서버 에러 메시지 처리
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            authViewModel.clearError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TripMate 계정 만들기",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start).padding(top = 20.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 이메일
            OutlinedTextField(
                value = authViewModel.email,
                onValueChange = { authViewModel.onEmailChanged(it) },
                label = { Text("이메일") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                isError = authViewModel.emailError != null,
                supportingText = {
                    authViewModel.emailError?.let { Text(it) }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 비밀번호
            OutlinedTextField(
                value = authViewModel.password,
                onValueChange = { authViewModel.onPasswordChanged(it) },
                label = { Text("비밀번호 (6자 이상)") },
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

            Spacer(modifier = Modifier.height(16.dp))

            // 닉네임
            OutlinedTextField(
                value = authViewModel.nickname,
                onValueChange = { authViewModel.onNicknameChanged(it) },
                label = { Text("닉네임") },
                modifier = Modifier.fillMaxWidth(),
                isError = authViewModel.nicknameError != null,
                supportingText = {
                    authViewModel.nicknameError?.let { Text(it) }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 약관 동의
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isAgreed = !isAgreed }
            ) {
                Checkbox(checked = isAgreed, onCheckedChange = { isAgreed = it })
                Text(text = "[필수] 서비스 이용약관 및 개인정보 처리방침 동의", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 가입 버튼
            Button(
                onClick = { authViewModel.signUp() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MainBlue),
                enabled = authViewModel.isSignUpValid && isAgreed && !isLoading
            ) {
                Text("회원 가입하기", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            TextButton(onClick = { onBack() }) {
                Text("취소하고 돌아가기", color = Color.Gray)
            }
        }


        if (isLoading) {
            LoadingOverlay()
        }
    }
}