package com.han.tripmate.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.han.tripmate.ui.theme.MainBlue
import com.han.tripmate.ui.viewmodel.ChatViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.han.tripmate.ui.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    guideId: String,
    viewModel: ChatViewModel = viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val messages = viewModel.messages
    val uiState by viewModel.uiState.collectAsState()

    val listState = rememberLazyListState()

    val auth = remember { FirebaseAuth.getInstance() }
    val myId = auth.currentUser?.uid ?: ""
    val chatRoomId = "room_${myId}_${guideId}"

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.uploadChatImage(chatRoomId, it) }
    }

    LaunchedEffect(chatRoomId) {
        viewModel.observeMessages(chatRoomId)
    }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Error) {
            Toast.makeText(context, (uiState as UiState.Error).message, Toast.LENGTH_SHORT).show()
            viewModel.resetUiState()
        }
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
                }
            )
        },
        bottomBar = {
            val isLoading = uiState is UiState.Loading

            Surface(tonalElevation = 3.dp) {
                Row(
                    modifier = Modifier.padding(8.dp).fillMaxWidth().navigationBarsPadding().imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { galleryLauncher.launch("image/*") },
                        enabled = !isLoading
                    ) {
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
                        enabled = viewModel.messageText.isNotBlank() && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MainBlue
                            )
                        } else {
                            Text(
                                "전송",
                                color = if (viewModel.messageText.isNotBlank()) MainBlue else Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFBACEE0)),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(messages) { message ->
                ChatBubble(
                    message = message,
                    isUser = message.senderId == myId
                )
            }
        }
    }
}