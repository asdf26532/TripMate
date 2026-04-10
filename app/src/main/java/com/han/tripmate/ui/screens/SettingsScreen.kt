package com.han.tripmate.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.han.tripmate.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    onNavigateToLogin: () -> Unit
) {
    var showDetailDialog by remember { mutableStateOf<String?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.uploadProfileImage(it) }
    }

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
                ProfileCard(
                    profileUrl = viewModel.profileImageUrl,
                    onEditClick = { galleryLauncher.launch("image/*") }
                )
            }

            // 일반 설정
            item {
                SettingSection(header = "일반 설정") {
                    SettingItemWithSwitch(
                        icon = Icons.Default.Notifications,
                        title = "알림 설정",
                        subtitle = "푸시 알림 및 이벤트 알림",
                        checked = viewModel.isNotificationsEnabled,
                        onCheckedChange = { viewModel.toggleNotifications(it) }
                    )
                    SettingItemWithSwitch(
                        icon = Icons.Default.DarkMode,
                        title = "다크 모드",
                        subtitle = "앱 배경 테마 변경",
                        checked = viewModel.isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode(it) }
                    )
                    SettingItem(
                        icon = Icons.Default.Language,
                        title = "언어 설정",
                        subtitle = "한국어",
                        onClick = { showDetailDialog = "언어 설정" }
                    )
                }
            }

            // 계정 및 보안
            item {
                SettingSection(header = "계정 및 보안") {
                    SettingItem(
                        icon = Icons.Default.Lock,
                        title = "비밀번호 변경",
                        onClick = { showDetailDialog = "비밀번호 변경" }
                    )
                    SettingItem(
                        icon = Icons.Default.Security,
                        title = "개인정보 처리방침",
                        onClick = { showDetailDialog = "개인정보 처리방침" }
                    )
                }
            }

            // 앱 정보
            item {
                SettingSection(header = "앱 정보") {
                    SettingItem(
                        icon = Icons.Default.Info,
                        title = "버전 정보",
                        subtitle = "1.0.0 (최신버전)",
                        onClick = { showDetailDialog = "버전 정보" }
                    )
                    SettingItem(
                        icon = Icons.Default.BugReport,
                        title = "문의하기 / 피드백",
                        onClick = { showDetailDialog = "문의하기" }
                    )
                    SettingItem(
                        icon = Icons.Default.Description,
                        title = "오픈소스 라이선스",
                        onClick = { showDetailDialog = "오픈소스 라이선스" }
                    )
                }
            }

            // 계정 관리 버튼
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextButton(
                        onClick = { viewModel.signOut { onNavigateToLogin() } },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("로그아웃", color = Color.Gray, fontWeight = FontWeight.Medium)
                    }
                    TextButton(
                        onClick = { viewModel.deleteAccount { onNavigateToLogin() } },
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

    // 상세 설정 클릭 시 팝업
    if (showDetailDialog != null) {
        AlertDialog(
            onDismissRequest = { showDetailDialog = null },
            title = { Text(showDetailDialog!!, fontWeight = FontWeight.Bold) },
            text = { Text("구현 예정") },
            confirmButton = {
                TextButton(onClick = { showDetailDialog = null }) { Text("확인") }
            }
        )
    }
}

@Composable
fun ProfileCard(profileUrl: String?, onEditClick: () -> Unit) {
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
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                if (profileUrl != null) {
                    AsyncImage(
                        model = profileUrl,
                        contentDescription = null,
                        modifier = Modifier.clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.padding(12.dp),
                        tint = Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(start = 20.dp)) {
                Text(text = "여행자님", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = "tripmate@example.com", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    onClick = onEditClick,
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
fun SettingItemWithSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium) },
        supportingContent = subtitle?.let { { Text(it, fontSize = 13.sp, color = Color.Gray) } },
        leadingContent = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        headlineContent = { Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium) },
        supportingContent = subtitle?.let { { Text(it, fontSize = 13.sp, color = Color.Gray) } },
        leadingContent = {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        },
        trailingContent = {
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}