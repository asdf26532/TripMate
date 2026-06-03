package com.han.tripmate.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewWriteScreen(
    guideName: String = "",
    onBack: () -> Unit = {},
    onSubmitSuccess: (Int, String, List<Uri>) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current

    var rating by remember { mutableIntStateOf(5) }
    var reviewText by remember { mutableStateOf("") }

    val selectedImages = remember { mutableStateListOf<Uri>() }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (selectedImages.size + uris.size > 5) {
            Toast.makeText(context, "사진은 최대 5장까지 첨부할 수 있습니다.", Toast.LENGTH_SHORT).show()
            val allowedCount = 5 - selectedImages.size
            if (allowedCount > 0) {
                selectedImages.addAll(uris.take(allowedCount))
            }
        } else {
            selectedImages.addAll(uris)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("리뷰 작성", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                color = Color.White,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Button(

                    onClick = {
                        if (reviewText.isBlank() || reviewText.length < 10) {
                            Toast.makeText(context, "최소 10자 이상의 생생한 후기를 남겨주세요.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "리뷰가 성공적으로 등록되었습니다!", Toast.LENGTH_SHORT).show()
                            onSubmitSuccess(rating, reviewText, selectedImages.toList())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MainBlue)
                ) {
                    Text("리뷰 등록 완료", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = guideName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "가이드와의 여행은 어떠셨나요?",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                for (i in 1..5) {
                    val isSelected = i <= rating
                    val starColor by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFFFFB200) else Color.LightGray.copy(alpha = 0.6f),
                        label = "StarColorAnimate"
                    )

                    Icon(
                        imageVector = if (isSelected) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "$i 점 선택",
                        tint = starColor,
                        modifier = Modifier
                            .size(44.dp)
                            .clickable { rating = i }
                    )
                }
            }

            Text(
                text = when (rating) {
                    1 -> "아쉬웠어요 😢"
                    2 -> "보통이에요 😐"
                    3 -> "무난했어요 🙂"
                    4 -> "만족스러워요 🥰"
                    else -> "최고의 가이드였어요! 🤩"
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MainBlue,
                modifier = Modifier.padding(bottom = 28.dp)
            )

            OutlinedTextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                placeholder = { Text("현지 가이드와의 만남, 이동 코스 등 도움이 되는 후기를 솔직하게 남겨주세요. (최소 10자)", fontSize = 14.sp, lineHeight = 20.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MainBlue,
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.7f),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 사진 첨부 인디케이터 헤더 영역
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "사진 첨부", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Text(text = "${selectedImages.size} / 5", fontSize = 13.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 갤러리 이미지 추가 슬롯 및 취소 뱃지 그리드 가로 리스트
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 사진 추가 버튼 트리거 슬롯
                if (selectedImages.size < 5) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .clickable { galleryLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = "사진 추가", tint = Color.Gray, modifier = Modifier.size(26.dp))
                    }
                }

                // 선택된 썸네일 이미지 목록 렌더링
                selectedImages.forEachIndexed { index, uri ->
                    Box(
                        modifier = Modifier.size(68.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "첨부 이미지",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.LightGray),
                            contentScale = ContentScale.Crop
                        )
                        // 개별 사진 삭제 취소 뱃지 버튼
                        Box(
                            modifier = Modifier
                                .offset(x = 4.dp, y = (-4).dp)
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .clickable { selectedImages.removeAt(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "삭제", tint = Color.White, modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReviewWriteScreenPreview() {
    MaterialTheme {
        ReviewWriteScreen()
    }
}