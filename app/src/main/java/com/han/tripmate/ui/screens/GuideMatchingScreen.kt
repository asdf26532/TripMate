package com.han.tripmate.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.han.tripmate.ui.theme.MainBlue

// 가상 데이터 모델 정의
data class GuideProfile(
    val id: String,
    val name: String,
    val rating: Double,
    val reviewCount: Int,
    val introduction: String,
    val pricePerDay: String,
    val profileUrl: String,
    val location: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideMatchingScreen(
    onBack: () -> Unit = {},
    onNavigateToChat: (String, String, String) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current

    var selectedLocation by remember { mutableStateOf("부산") }
    val guideList = remember(selectedLocation) {
        listOf(
            GuideProfile("1", "김민준 가이드", 4.9, 128, "야경 스팟 및 로컬 숨은 맛집 전문 투어!", "80,000원", "https://via.placeholder.com/150", selectedLocation),
            GuideProfile("2", "이서연 가이드", 4.8, 94, "역사 문화 해설부터 감성 사진 촬영까지 완료", "95,000원", "https://via.placeholder.com/150", selectedLocation),
            GuideProfile("3", "박준영 가이드", 4.7, 56, "뚜벅이 여행자를 위한 맞춤형 차량 가이드", "120,000원", "https://via.placeholder.com/150", selectedLocation)
        )
    }

    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedGuide by remember { mutableStateOf<GuideProfile?>(null) }

    val sheetState = rememberModalBottomSheetState()
    var guestCount by remember { mutableStateOf("2") }
    var userRequest by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("$selectedLocation 가이드 찾기", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FA))
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("부산", "서울", "제주", "경주").forEach { location ->
                    val isSelected = selectedLocation == location
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) MainBlue else Color(0xFFF1F3F5))
                            .clickable { selectedLocation = location }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = location,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color.Gray
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(guideList, key = { it.id }) { guide ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedGuide = guide
                                showBottomSheet = true
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = guide.profileUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = guide.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB200), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(text = "${guide.rating}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "(${guide.reviewCount})", fontSize = 12.sp, color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = guide.introduction,
                                    fontSize = 13.sp,
                                    color = Color.DarkGray,
                                    maxLines = 1
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "1일 기준", fontSize = 10.sp, color = Color.Gray)
                                Text(text = guide.pricePerDay, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MainBlue)
                            }
                        }
                    }
                }
            }
        }


        if (showBottomSheet && selectedGuide != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "${selectedGuide!!.name}에게 매칭 요청",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(20.dp))


                    OutlinedTextField(
                        value = guestCount,
                        onValueChange = { guestCount = it },
                        label = { Text("여행 인원 (명)") },
                        leadingIcon = { Icon(Icons.Default.Group, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))


                    OutlinedTextField(
                        value = userRequest,
                        onValueChange = { userRequest = it },
                        label = { Text("가이드에게 바라는 점 (선택)") },
                        leadingIcon = { Icon(Icons.Default.Message, contentDescription = null) },
                        placeholder = { Text("예) 부모님과 함께하는 효도 관광이에요. 편안한 코스로 부탁드립니다.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.height(24.dp))


                    Button(
                        onClick = {
                            showBottomSheet = false
                            Toast.makeText(context, "${selectedGuide!!.name}에게 매칭 요청서가 전달되었습니다!", Toast.LENGTH_SHORT).show()

                            onNavigateToChat(selectedGuide!!.id, selectedGuide!!.name, selectedGuide!!.profileUrl)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MainBlue)
                    ) {
                        Text("매칭 요청서 보내기", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GuideMatchingScreenPreview() {
    MaterialTheme {
        GuideMatchingScreen()
    }
}