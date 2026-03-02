package com.han.tripmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.han.tripmate.ui.theme.MainBlue
import com.han.tripmate.ui.theme.TripMateTheme

@Composable
fun HomeScreen() {
    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 환영 헤더
        item {
            Text(
                text = "어디로 떠나볼까요?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp, top = 16.dp)
            )
        }
        // 인기 여행지 (가로 스크롤 섹션)
        item {
            Text(
                text = "인기 여행지",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(5) { index ->
                    TravelCard("추천 여행지 ${index + 1}")
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
        // 근처 여행지
        item {
            Text(
                text = "내 근처 추천",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        items(10) { index ->
            NearbyItem("근처 명소 ${index + 1}")
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.3f))
        }


    }
}

@Composable
fun TravelCard(title: String) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 임시 배경색
            Box(modifier = Modifier
                .fillMaxSize()
                .background(MainBlue.copy(alpha = 0.2f)))

            Text(
                text = title,
                modifier = Modifier
                    .padding(12.dp)
                    .align(androidx.compose.ui.Alignment.BottomStart),
                fontWeight = FontWeight.Bold
            )
        }

    }
}

@Composable
fun NearbyItem(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Place,
                contentDescription = null,
                tint = Color.White
            )
        }
        Column {
            Text(text = title, fontWeight = FontWeight.SemiBold)
            Text(text = "상세 설명", fontSize = 12.sp, color = Color.Gray)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TripMateTheme {
        HomeScreen()
    }
}