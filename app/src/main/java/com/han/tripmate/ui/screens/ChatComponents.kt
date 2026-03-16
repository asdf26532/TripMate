package com.han.tripmate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.han.tripmate.data.model.Message
import com.han.tripmate.ui.theme.MainBlue

@Composable
fun ChatBubble(message: Message, isUser: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isUser) MainBlue else Color.White,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 2.dp
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                color = if (isUser) Color.White else Color.Black
            )
        }
        // 시간 표시
        Text(text = message.timestamp, fontSize = 10.sp, color = Color.Gray)
    }
}