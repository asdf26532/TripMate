package com.han.tripmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.han.tripmate.data.TravelService
import com.han.tripmate.data.model.UserRole
import com.han.tripmate.ui.theme.MainBlue
import com.han.tripmate.ui.theme.TripMateTheme
import com.han.tripmate.ui.viewmodel.AuthViewModel
import com.han.tripmate.ui.viewmodel.TravelViewModel

@Composable
fun HomeScreen(authViewModel: AuthViewModel,
               travelViewModel: TravelViewModel = viewModel(),
               navController: NavHostController) {

    val user by authViewModel.currentUser.collectAsState()

    if (user?.currentRole == UserRole.GUIDE) {
        GuideDashboard()
    } else {
        TravelerHome(authViewModel, travelViewModel, navController)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_service") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.offset(y = 50.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "가이드 등록")
            }
        },
        floatingActionButtonPosition = FabPosition.End // 오른쪽 하단 배치
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            TravelerHome(authViewModel, travelViewModel, navController)
        }
    }
}

@Composable
fun TravelerHome(
    authViewModel: AuthViewModel,
    travelViewModel: TravelViewModel = viewModel(),
    navController: NavHostController
) {

    val user by authViewModel.currentUser.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val serviceList by travelViewModel.services.collectAsState()
    val favoriteIds by travelViewModel.favoriteIds.collectAsState()

    val filteredServices = remember(searchQuery, serviceList) {
        if (searchQuery.isEmpty()) {
            serviceList
        } else {
            serviceList.filter { service ->
                service.title.contains(searchQuery, ignoreCase = true) ||
                        service.location.contains(searchQuery, ignoreCase = true) ||
                        service.category.contains(searchQuery, ignoreCase = true)
            }
        }
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
        // 검색
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("지역, 서비스, 가이드 검색") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "지우기")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }
        items(filteredServices) { service ->
            ServiceListItem(
                service = service,
                isFavorite = favoriteIds.contains(service.id),
                onFavoriteClick = { travelViewModel.toggleFavorite(service.id) },
                onItemClick = { id -> navController.navigate("detail/$id") }
            )
        }
        if (filteredServices.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("검색 결과가 없습니다.", color = Color.Gray)
                }
            }
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
    }
}

@Composable
fun GuideDashboard() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "가이드 관리 센터",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        // 요약 카드 (수익, 예약 등)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DashboardCard("이번 달 수익", "₩ 1,250,000", Modifier.weight(1f))
            DashboardCard("새 예약", "3건", Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. 서비스 관리 메뉴
        Text(text = "내 서비스 관리", fontWeight = FontWeight.Bold)

        Card(modifier = Modifier.fillMaxWidth()) {
            ListItem(
                headlineContent = { Text("파리 비즈니스 통역 서비스") },
                supportingContent = { Text("현재 노출 중 · 평점 4.9") },
                trailingContent = { Text("수정 >", color = Color.Gray) }
            )
        }

        Button(
            onClick = { /* 새 서비스 등록 */ },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("+ 새 여행 서비스 등록하기")
        }
    }
}

@Composable
fun DashboardCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MainBlue.copy(alpha = 0.1f))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 12.sp, color = MainBlue)
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
fun ServiceListItem(
    service: TravelService,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onItemClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onItemClick(service.id) },
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 이미지 영역 (Coil 사용)
        AsyncImage(
            model = service.thumbnailUrl,
            contentDescription = null,
            modifier = Modifier
                .size(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )

        // 정보 영역
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
        // 찜 아이콘
        Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = null,
            tint = if (isFavorite) Color.Red else Color.LightGray,
            modifier = Modifier
                .size(24.dp)
                .clickable { onFavoriteClick() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TripMateTheme {
        val mockNavController = rememberNavController()
        Column {
            Text("프리뷰 모드: UI 확인용", modifier = Modifier.padding(16.dp))
            ServiceListItem(
                service = TravelService(
                    id = "1", authorId = "expert_01", title = "프리뷰 서비스", location = "서울",
                    category = "가이드", price = 10000, priceUnit = "시간",
                    rating = 4.5, reviewCount = 10, thumbnailUrl = "",
                    isVerified = false, tags = listOf("태그1", "태그2")
                ),
                isFavorite = true,
                onFavoriteClick = {},
                onItemClick = {}
            )
        }
    }
}