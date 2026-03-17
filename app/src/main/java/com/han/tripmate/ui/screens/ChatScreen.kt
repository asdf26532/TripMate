package com.han.tripmate.ui.screens

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    guideId: String,
    viewModel: ChatViewModel = viewModel(),
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 가이드 프로필 (임시)
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
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.padding(8.dp))
                    Icon(Icons.Default.Menu, contentDescription = null, modifier = Modifier.padding(8.dp))
                }
            )
        },
        bottomBar = {
            // 입력창
            Row(
                modifier = Modifier.padding(8.dp).fillMaxWidth().navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.padding(4.dp))
                OutlinedTextField(
                    value = viewModel.messageText,
                    onValueChange = { viewModel.updateMessageText(it) },
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    placeholder = { Text("메시지 입력") },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray)
                )
                TextButton(onClick = { viewModel.sendMessage() }) {
                    Text("전송", color = MainBlue, fontWeight = FontWeight.Bold)
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