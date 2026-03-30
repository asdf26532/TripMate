package com.han.tripmate.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.han.tripmate.data.model.Message
import com.han.tripmate.ui.theme.MainBlue
import com.han.tripmate.ui.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    guideId: String,
    viewModel: ChatViewModel = viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val auth = remember { FirebaseAuth.getInstance() }
    val currentUser = auth.currentUser
    val myId = currentUser?.uid ?: ""

    if (currentUser == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("로그인이 필요한 서비스입니다.")
        }
        return
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.uploadChatImage(context, it) }
    }

    val chatRoomId = "room_${myId}_${guideId}"

    LaunchedEffect(chatRoomId) {
        viewModel.observeMessages(chatRoomId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(36.dp), shape = CircleShape, color = Color.LightGray) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "$guideId 가이드", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { /* 검색 로직 */ }) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.padding(8.dp))
                    }
                    IconButton(onClick = { /* 메뉴 로직 */ }) {
                        Icon(Icons.Default.Menu, contentDescription = null, modifier = Modifier.padding(8.dp))
                    }
                }
            )
        },
        bottomBar = {
            // 입력창
            Surface(tonalElevation = 3.dp) {
                Row(
                    modifier = Modifier.padding(8.dp).fillMaxWidth().navigationBarsPadding().imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // + 버튼 클릭 시 갤러리 실행
                    IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                        Icon(Icons.Default.Add, contentDescription = "사진 추가", tint = Color.Gray)
                    }

                    OutlinedTextField(
                        value = viewModel.messageText,
                        onValueChange = { viewModel.updateMessageText(it) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                        placeholder = { Text("메시지 입력", fontSize = 14.sp) },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.LightGray,
                            focusedBorderColor = MainBlue
                        ),
                        maxLines = 3
                    )

                    TextButton(
                        onClick = { viewModel.sendMessage(chatRoomId) },
                        enabled = viewModel.messageText.isNotBlank()
                    ) {
                        Text("전송", color = if (viewModel.messageText.isNotBlank()) MainBlue else Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFBACEE0)),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(viewModel.messages) { message ->
                ChatBubble(message = message, isUser = message.senderId == "my_id")
            }
        }
    }
}