package com.han.tripmate.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
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
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(10) // 최대 10장
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedImages = uris // 선택된 이미지들 저장
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("가이드 서비스 등록") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로가기")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 사진 등록
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp))
                        .clickable {
                            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddAPhoto, null)
                        Text("0/10", fontSize = 12.sp)
                    }
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(selectedImages) { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            // 제목 & 카테고리
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("글 제목") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("카테고리 (예: 통역, 맛집탐방)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("활동 지역 (예: 프랑스 파리)") }, modifier = Modifier.fillMaxWidth())

            // 시급 (가격)
            OutlinedTextField(
                value = price,
                onValueChange = { if (it.all { char -> char.isDigit() }) price = it },
                label = { Text("시급 (원)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefix = { Text("₩ ") }
            )

            // 상세 설명
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("서비스 상세 설명") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 등록 버튼
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
                        description = description,
                        priceUnit = "시간당",
                        thumbnailUrl = if (selectedImages.isNotEmpty()) selectedImages[0].toString() else "", // 첫 번째 사진을 썸네일로 (임시)
                        tags = listOf("신규등록"),
                        createdAt = com.google.firebase.Timestamp.now()
                    )
                    travelViewModel.addService(newService) { success ->
                        if (success) {
                            Toast.makeText(context, "가이드 서비스가 성공적으로 등록되었습니다!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
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