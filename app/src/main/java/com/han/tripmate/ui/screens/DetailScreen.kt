package com.han.tripmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.han.tripmate.ui.theme.TripMateTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.han.tripmate.ui.theme.MainBlue
import com.han.tripmate.ui.viewmodel.TravelViewModel

@Composable
fun DetailScreen(
    serviceId: String,
    travelViewModel: TravelViewModel,
    onBack: () -> Unit,
    onChatClick: (String) -> Unit
) {
    val services by travelViewModel.services.collectAsState()
    val favoriteIds by travelViewModel.favoriteIds.collectAsState()
    val service = services.find { it.id == serviceId } ?: return
    val isFavorite = favoriteIds.contains(serviceId)

    Scaffold(
        bottomBar = {
            Surface(
                shadowElevation = 16.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding(), // 하단 시스템 바 겹침 방지
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    //  왼쪽: 가격 정보
                    Column {
                        Text(text = "결제 금액", fontSize = 12.sp, color = Color.Gray)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${String.format("%,d", service.price)}원",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MainBlue
                            )
                            Text(text = " / ${service.priceUnit}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }

                    // 오른쪽: 채팅하기 버튼
                    Button(
                        onClick = { onChatClick(service.authorId) },
                        modifier = Modifier
                            .width(180.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MainBlue)
                    ) {
                        Text("채팅하기", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            //  큰 이미지 영역
            AsyncImage(
                model = "https://images.unsplash.com/photo-1502602898657-3e91760cbb34", // 파리 예시
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(300.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(20.dp)) {
                // 카테고리 & 위치
                Text(text = "비즈니스/통역 · 프랑스 파리", color = Color.Gray, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(8.dp))

                // 제목
                Text(
                    text = "파리 비즈니스 전문 통역 및 VIP 의전 가이드 (벤츠 제공)",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 30.sp
                )

                Text(
                    text = "상품번호: $serviceId",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 가격 정보
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = "45,000원", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MainBlue)
                    Text(text = " / 시간당", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

                // 상세 설명 (임시)
                Text(text = "서비스 상세 설명", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "파리 현지에서 10년 이상 거주한 전문가가 비즈니스 미팅 통역부터 VIP 의전까지 완벽하게 지원합니다. 벤츠 S클래스 차량이 기본 제공되며, 공항 픽업부터 호텔 체크인까지 도와드립니다.",
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(100.dp)) // 버튼에 가려지지 않게 여백
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 5.dp, end = 16.dp), // statusBarsPadding 대신 여백 조정
            horizontalArrangement = Arrangement.SpaceBetween // 양 끝으로 배치
        ) {
            // 상단 뒤로가기 버튼 (이미지 위에 띄우기)
            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(top = 25.dp, start = 5.dp).background(Color.Black.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기", tint = Color.White)
            }

            // 찜하기 버튼
            IconButton(
                onClick = { travelViewModel.toggleFavorite(serviceId) },
                modifier = Modifier.padding(top = 25.dp).background(Color.Black.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorite) Color.Red else Color.White
                )
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    TripMateTheme {
        DetailScreen(
            serviceId = "1",
            onBack = {}
        )
    }
}*/
