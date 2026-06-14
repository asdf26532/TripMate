package com.han.tripmate.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.han.tripmate.ui.theme.MainBlue
import com.han.tripmate.ui.viewmodel.TravelViewModel

@Composable
fun DetailScreen(
    serviceId: String,
    travelViewModel: TravelViewModel,
    onBack: () -> Unit,
    onChatClick: (String) -> Unit
) {
    val context = LocalContext.current
    val services by travelViewModel.services.collectAsState()
    val favoriteIds by travelViewModel.favoriteIds.collectAsState()
    val service = services.find { it.id == serviceId } ?: return
    val isFavorite = favoriteIds.contains(serviceId)

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val isMyService = service.authorId == currentUserId

    var menuExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    var editTitle by remember { mutableStateOf(service.title) }
    var editDescription by remember { mutableStateOf(service.description) }
    var editPrice by remember { mutableStateOf(service.price.toString()) }

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
            if (!service.images.isNullOrEmpty()) {
                AsyncImage(
                    model = service.images,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.LightGray.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = service.title,
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
                    Text(
                        text = "${String.format("%,d", service.price)}원",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MainBlue
                    )
                    Text(
                        text = " / ${service.priceUnit}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

                // 상세 설명
                Text(text = "서비스 상세 설명", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = service.description,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 5.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 왼쪽: 뒤로가기 버튼
            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(top = 25.dp, start = 5.dp).background(Color.Black.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기", tint = Color.White)
            }

            // 오른쪽: 액션 영역 (본인 글이면 수정/삭제 메뉴, 타인 글이면 찜하기 버튼)
            Row(modifier = Modifier.padding(top = 25.dp)) {
                if (isMyService) {
                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        ) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "더보기", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("수정하기") },
                                onClick = {
                                    menuExpanded = false
                                    editTitle = service.title
                                    editDescription = service.description
                                    editPrice = service.price.toString()
                                    showEditDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("삭제하기", color = Color.Red) },
                                onClick = {
                                    menuExpanded = false
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                } else {
                    IconButton(
                        onClick = { travelViewModel.toggleFavorite(serviceId) },
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.3f), CircleShape)
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
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("서비스 삭제", fontWeight = FontWeight.Bold) },
            text = { Text("정말로 이 가이드 서비스를 삭제하시겠습니까?\n삭제된 데이터는 복구할 수 없습니다.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        travelViewModel.deleteService(
                            serviceId = serviceId,
                            onSuccess = {
                                Toast.makeText(context, "성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                                onBack() // 삭제 완료 후 이전 화면으로 튕기기
                            },
                            onFailure = {
                                Toast.makeText(context, "삭제 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("삭제") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("취소") }
            }
        )
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("서비스 정보 수정", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("서비스 제목") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPrice,
                        onValueChange = { editPrice = it },
                        label = { Text("가격 (원)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDescription,
                        onValueChange = { editDescription = it },
                        label = { Text("상세 설명") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 5
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val priceInt = editPrice.toIntOrNull()
                        if (editTitle.isBlank() || editDescription.isBlank() || priceInt == null) {
                            Toast.makeText(context, "입력값을 다시 확인해 주세요.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        showEditDialog = false
                        travelViewModel.updateService(
                            serviceId = serviceId,
                            updatedTitle = editTitle,
                            updatedDescription = editDescription,
                            updatedPrice = priceInt,
                            onSuccess = {
                                Toast.makeText(context, "글이 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = {
                                Toast.makeText(context, "수정 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MainBlue)
                ) { Text("수정하기") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("취소") }
            }
        )
    }
}

