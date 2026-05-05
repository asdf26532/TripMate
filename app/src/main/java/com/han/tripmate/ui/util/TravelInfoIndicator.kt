package com.han.tripmate.ui.util

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TravelInfoIndicator(
    duration: String,
    distance: String,
    transportMode: String = "자동차"
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Spacer(modifier = Modifier.width(44.dp))

        Surface(
            color = Color(0xFFF1F3F5),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "🚗 ${transportMode}로 $duration ($distance) 이동",
                fontSize = 11.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}