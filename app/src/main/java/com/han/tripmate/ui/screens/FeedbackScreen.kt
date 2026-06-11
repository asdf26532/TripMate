package com.han.tripmate.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.han.tripmate.ui.theme.MainBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    var feedbackText by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("문의하기 / 피드백", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "TripMate를 이용하며 느끼신 불편한 점이나 제안하고 싶은 아이디어가 있다면 자유롭게 작성해 주세요. 개발자에게 직접 전달됩니다.",
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp
            )

            OutlinedTextField(
                value = userEmail,
                onValueChange = { userEmail = it },
                label = { Text("답변받을 이메일 주소 (선택)") },
                placeholder = { Text("example@email.com") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = feedbackText,
                onValueChange = { feedbackText = it },
                label = { Text("문의 및 의견 내용") },
                placeholder = { Text("여기에 의견을 입력해 주세요. (최소 5자 이상)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                maxLines = 10
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (feedbackText.trim().length < 5) {
                        Toast.makeText(context, "의견을 5자 이상 입력해 주세요.", Toast.LENGTH_SHORT).show()
                    } else {
                        executeSendEmail(context, userEmail, feedbackText)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MainBlue)
            ) {
                Text("의견 보내기", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun executeSendEmail(context: Context, replyEmail: String, content: String) {
    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf("test@tesl.com"))
        putExtra(Intent.EXTRA_SUBJECT, "[TripMate] 유저 피드백 및 문의사항")
        putExtra(
            Intent.EXTRA_TEXT,
            "---------------------------\n" +
                    "앱 버전: 1.0.0\n" +
                    "패키지명: com.han.tripmate\n" +
                    "회신받을 메일: ${replyEmail.ifBlank { "미기입" }}\n" +
                    "---------------------------\n\n" +
                    "내용:\n$content"
        )
    }

    try {
        context.startActivity(Intent.createChooser(emailIntent, "메일 전송 앱 선택"))
    } catch (e: Exception) {
        Toast.makeText(context, "메일을 보낼 수 있는 앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
    }
}