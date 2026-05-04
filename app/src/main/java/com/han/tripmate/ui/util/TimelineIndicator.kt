package com.han.tripmate.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.han.tripmate.ui.theme.MainBlue

@Composable
fun TimelineIndicator(
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(2.dp)
                .weight(1f)
                .background(if (isFirst) Color.Transparent else Color.LightGray)
        )
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(MainBlue, shape = CircleShape)
        )
        Box(
            modifier = Modifier
                .width(2.dp)
                .weight(1f)
                .background(if (isLast) Color.Transparent else Color.LightGray)
        )
    }
}