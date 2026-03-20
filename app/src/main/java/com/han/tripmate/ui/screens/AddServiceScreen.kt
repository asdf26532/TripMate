package com.han.tripmate.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.han.tripmate.data.TravelService
import com.han.tripmate.ui.viewmodel.AuthViewModel
import com.han.tripmate.ui.viewmodel.TravelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddServiceScreen(
    authViewModel: AuthViewModel,
    travelViewModel: TravelViewModel,
    navController: NavHostController
) {
    val user by authViewModel.currentUser.collectAsState()

    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("가이드 서비스 등록") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "뒤로가기")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. 사진 등록 (임시 아이콘)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp))
                    .clickable { /* 사진 선택 로직 */ },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddAPhoto, null)
                    Text("0/10", fontSize = 12.sp)
                }
            }

            // 2. 제목 & 카테고리
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("글 제목") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("카테고리 (예: 통역, 맛집탐방)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("활동 지역 (예: 프랑스 파리)") }, modifier = Modifier.fillMaxWidth())

            // 3. 시급 (가격)
            OutlinedTextField(
                value = price,
                onValueChange = { if (it.all { char -> char.isDigit() }) price = it },
                label = { Text("시급 (원)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // 4. 상세 설명
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("서비스 상세 설명") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 5. 등록 버튼
            Button(
                onClick = {
                    if (title.isBlank() || location.isBlank() || category.isBlank() || price.isEmpty() || description.isBlank()) {
                        Toast.makeText(context, "모든 정보를 입력해주세요!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val newService = TravelService(
                        authorId = user?.id ?: "",
                        title = title,
                        location = location,
                        category = category,
                        price = price.toLongOrNull() ?: 0L,
                        priceUnit = "시간당",
                        thumbnailUrl = "https://images.unsplash.com/photo-1502602898657-3e91760cbb34", // 임시 사진
                        tags = listOf("신규등록"),
                        createdAt = com.google.firebase.Timestamp.now() // 작성일 추가
                    )
                    travelViewModel.addService(newService) { success ->
                        if (success) {
                            Toast.makeText(context, "가이드 서비스가 성공적으로 등록되었습니다!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            // 💡 2. 발생 가능한 에러 예상 (네트워크 오류 등)
                            Toast.makeText(context, "등록에 실패했습니다. 인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("작성 완료", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}