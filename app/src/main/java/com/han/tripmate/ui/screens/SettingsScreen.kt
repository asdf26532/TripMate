package com.han.tripmate.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("설정", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 프로필
            item {
                ProfileSection()
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
            }

            // 일반 설정
            item {
                SettingHeader("일반")
                SettingItem(icon = Icons.Default.Notifications, title = "알림 설정")
                SettingItem(icon = Icons.Default.Lock, title = "개인정보 및 보안")
                SettingItem(icon = Icons.Default.Palette, title = "테마 설정", subtitle = "시스템 기본값")
            }

            // 앱 정보 섹션
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingHeader("정보")
                SettingItem(icon = Icons.Default.Info, title = "버전 정보", subtitle = "1.0.0 (Latest)")
                SettingItem(icon = Icons.Default.Description, title = "오픈소스 라이선스")
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "com.han.tripmate",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 임시 프로필 이미지 아이콘
        Surface(
            modifier = Modifier.size(60.dp),
            shape = androidx.compose.foundation.shape.CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.padding(12.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(text = "여행자님", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = "tripmate@example.com", fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun SettingHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null
) {
    ListItem(
        modifier = Modifier.clickable { /* 클릭 시 이동 로직 */ },
        headlineContent = { Text(title, fontSize = 16.sp) },
        supportingContent = subtitle?.let { { Text(it, fontSize = 13.sp) } },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.DarkGray,
                modifier = Modifier.size(24.dp)
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    )
}