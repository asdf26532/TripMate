package com.han.tripmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.han.tripmate.data.model.ChatRoom
import com.han.tripmate.ui.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPreviewScreen(
    navController: NavHostController,
    viewModel: ChatViewModel = viewModel()
) {
    val chatRooms by viewModel.chatRooms.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.observeChatRoomList()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("채팅", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        if (chatRooms.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("진행 중인 채팅이 없습니다.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(chatRooms) { room ->
                    ChatRoomItem(room) {
                        navController.navigate("chat_screen/${room.otherUserId}")
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatRoomItem(room: ChatRoom, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = room.profileImageUrl.ifEmpty { "https://via.placeholder.com/150" },
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = room.otherNickname,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = room.getFormattedLastTime(),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = room.lastMessage,
                fontSize = 14.sp,
                color = Color.DarkGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}