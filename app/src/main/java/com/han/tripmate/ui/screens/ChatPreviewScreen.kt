package com.han.tripmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.han.tripmate.data.model.ChatRoom

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPreviewScreen(navController: NavHostController) {
    // 임시 데이터
    val chatRooms = listOf(
        ChatRoom("1", "guide_01", "파리 가이드 민수", "내일 오전 10시 에펠탑 앞에서 뵐게요!", "오후 2:30", ""),
        ChatRoom("2", "guide_02", "교토 현지인 사토", "예약 확인 감사합니다.", "어제", ""),
        ChatRoom("3", "user_99", "여행자 릴리", "비즈니스 통역 관련 문의드려요.", "3월 20일", "")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("채팅", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(chatRooms) { room ->
                ChatRoomItem(room) {
                    // 클릭 시 채팅방이동
                    navController.navigate("chat_screen/${room.otherUserId}")
                }
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color = Color.LightGray
                )
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
        // 프로필 이미지 (Coil 사용)
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

        // 마지막 메시지
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
                    text = room.lastTime,
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