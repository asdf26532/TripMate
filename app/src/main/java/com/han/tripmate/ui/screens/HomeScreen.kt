package com.han.tripmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.han.tripmate.data.TravelService
import com.han.tripmate.ui.theme.MainBlue
import com.han.tripmate.ui.theme.TripMateTheme
import com.han.tripmate.ui.viewmodel.AuthViewModel

@Composable
fun HomeScreen(authViewModel: AuthViewModel) {

    val user by authViewModel.currentUser.collectAsState()

    // 임시 데이터
    val serviceList = remember {
        listOf(
            TravelService(
                id = "1", authorId = "expert_01",
                title = "파리 비즈니스 전문 통역 및 VIP 의전 가이드 (벤츠 제공)",
                location = "프랑스 파리", category = "비즈니스/통역",
                price = 45000, priceUnit = "시간당",
                rating = 4.9, reviewCount = 128,
                thumbnailUrl = "https://images.unsplash.com/photo-1502602898657-3e91760cbb34", // 파리 예시 사진
                isVerified = true, tags = listOf("통역전문", "차량지원")
            ),
            TravelService(
                id = "2", authorId = "expert_02",
                title = "도쿄 아키하바라 피규어&애니 투어 (현지인 단골샵 방문)",
                location = "일본 도쿄", category = "테마투어",
                price = 28000, priceUnit = "시간당",
                rating = 4.8, reviewCount = 56,
                thumbnailUrl = "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf", // 도쿄 예시 사진
                tags = listOf("덕질여행", "맛집탐방")
            ),
            TravelService(
                id = "3", authorId = "expert_03",
                title = "다낭 미케비치 서핑 레슨 및 로컬 스냅 촬영",
                location = "베트남 다낭", category = "액티비티",
                price = 35000, priceUnit = "1회당",
                rating = 5.0, reviewCount = 42,
                thumbnailUrl = "https://images.unsplash.com/photo-1559592413-7ece3593e103", // 다낭 예시 사진
                isVerified = true, tags = listOf("인생샷", "초보환영")
            )
        )
    }

    LazyColumn (
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 12.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "${user?.nickname ?: "회원" }님, 현지 전문가와 함께\n특별한 여행을 만들어보세요",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp,
                    color = Color.Gray)
            }
        }
        // 인기 여행지
        item {
            Text(
                text = "인기 여행지",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(5) { index ->
                    TravelCard("추천 여행지 ${index + 1}")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "현지 전문가 추천",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "TripMate가 검증한 가이드들을 만나보세요.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }

        items(serviceList) { service ->
            ServiceListItem(service)
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color.LightGray.copy(alpha = 0.2f))
        }
    }
}

@Composable
fun TravelCard(title: String) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(150.dp),
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
                    .align(Alignment.BottomStart),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ServiceListItem(service: TravelService) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { /* 상세 페이지로 이동 */ },
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. 이미지 영역 (Coil 사용)
        AsyncImage(
            model = service.thumbnailUrl,
            contentDescription = null,
            modifier = Modifier
                .size(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )

        // 2. 정보 영역
        Column(modifier = Modifier.weight(1f)) {
            // 카테고리 & 위치
            Text(
                text = "${service.category} · ${service.location}",
                fontSize = 12.sp,
                color = Color.Gray
            )

            // 서비스 제목
            Text(
                text = service.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                modifier = Modifier.padding(top = 2.dp)
            )

            // 가격
            Text(
                text = "${String.format("%,d", service.price)}원 / ${service.priceUnit}",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )

            // 평점 및 뱃지
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = " ${service.rating} (${service.reviewCount})",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                if (service.isVerified) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = MainBlue.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "전문가인증",
                            fontSize = 10.sp,
                            color = MainBlue,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // 3. 하트 아이콘
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TripMateTheme {
        val mockViewModel: AuthViewModel = viewModel()
        HomeScreen(authViewModel = mockViewModel)
    }
}