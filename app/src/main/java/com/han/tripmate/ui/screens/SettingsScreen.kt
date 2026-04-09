package com.han.tripmate.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
                title = { Text("설정", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
                ProfileCard()
            }

            // 일반 설정
            item {
                SettingSection(header = "일반 설정") {
                    SettingItem(
                        icon = Icons.Default.Notifications,
                        title = "알림 설정",
                        subtitle = "푸시 알림 및 이벤트 알림"
                    )
                    SettingItem(
                        icon = Icons.Default.Palette,
                        title = "테마 설정",
                        subtitle = "시스템 기본값"
                    )
                    SettingItem(
                        icon = Icons.Default.Language,
                        title = "언어 설정",
                        subtitle = "한국어"
                    )
                }
            }

            // 계정 및 보안
            item {
                SettingSection(header = "계정 및 보안") {
                    SettingItem(icon = Icons.Default.Lock, title = "비밀번호 변경")
                    SettingItem(icon = Icons.Default.Security, title = "개인정보 처리방침")
                }
            }

            // 앱 정보 및 고객 지원
            item {
                SettingSection(header = "앱 정보") {
                    SettingItem(icon = Icons.Default.Info, title = "버전 정보", subtitle = "1.0.0 (최신버전)")
                    SettingItem(icon = Icons.Default.BugReport, title = "문의하기 / 피드백")
                    SettingItem(icon = Icons.Default.Description, title = "오픈소스 라이선스")
                }
            }

            // 계정 관리 구역
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextButton(
                        onClick = { /* 로그아웃 로직 */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("로그아웃", color = Color.Gray, fontWeight = FontWeight.Medium)
                    }
                    TextButton(
                        onClick = { /* 회원탈퇴 로직 */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("회원탈퇴", color = Color.Red.copy(alpha = 0.6f), fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "com.han.tripmate",
                        fontSize = 11.sp,
                        color = Color.LightGray,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun ProfileCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 프로필 이미지 아바타
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = Color.White
                )
            }

            Column(modifier = Modifier.padding(start = 20.dp)) {
                Text(
                    text = "여행자님",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "tripmate@example.com",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 프로필 수정 미니 버튼
                Surface(
                    onClick = { /* 프로필 편집 페이지 이동 */ },
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "프로필 수정",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun SettingSection(header: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = header,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        content()
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            thickness = 0.5.dp,
            color = Color.LightGray.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null
) {
    ListItem(
        modifier = Modifier.clickable { /* 상세 설정 이동 */ },
        headlineContent = {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        },
        supportingContent = subtitle?.let {
            { Text(it, fontSize = 13.sp, color = Color.Gray) }
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}