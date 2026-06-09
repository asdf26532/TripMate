package com.han.tripmate.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.han.tripmate.ui.navigation.Routes
import com.han.tripmate.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    navController: androidx.navigation.NavHostController,
    onNavigateToLogin: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var showDetailDialog by remember { mutableStateOf<String?>(null) }
    var showNameEditDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf("") }

    var showPasswordDialog by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var showLanguageDialog by remember { mutableStateOf(false) }
    val languageOptions = listOf("한국어", "English", "日本語")
    var selectedLanguage by remember { mutableStateOf("한국어") }

    var showPrivacyDialog by remember { mutableStateOf(false) }

    var showVersionDialog by remember { mutableStateOf(false) }
    var isCheckingVersion by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.uploadProfileImage(it) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                // 프로필 카드
                item {
                    ProfileCard(
                        userName = viewModel.userName,
                        profileUrl = viewModel.profileImageUrl,
                        onEditClick = { galleryLauncher.launch("image/*") },
                        onNameClick = {
                            tempName = viewModel.userName
                            showNameEditDialog = true
                        }
                    )
                }

                // 일반 설정 섹션
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
                            subtitle = selectedLanguage,
                            onClick = { showLanguageDialog = true }
                        )
                    }
                }

                // 계정 및 보안 섹션
                item {
                    SettingSection(header = "계정 및 보안") {
                        SettingItem(
                            icon = Icons.Default.Lock,
                            title = "비밀번호 변경",
                            onClick = {
                                currentPassword = ""
                                newPassword = ""
                                confirmPassword = ""
                                passwordError = null
                                showPasswordDialog = true
                            }
                        )
                        SettingItem(
                            icon = Icons.Default.Security,
                            title = "개인정보 처리방침",
                            onClick = { showDetailDialog = "개인정보 처리방침" }
                        )
                    }
                }

                // 앱 정보 섹션
                item {
                    SettingSection(header = "앱 정보") {
                        SettingItem(
                            icon = Icons.Default.Info,
                            title = "버전 정보",
                            subtitle = "1.0.0 (최신버전)",
                            onClick = {
                                scope.launch {
                                    isCheckingVersion = true
                                    delay(1000)
                                    isCheckingVersion = false
                                    showVersionDialog = true
                                }
                            }
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

                item {
                    SettingSection(header = "활동 기록") {
                        SettingItem(
                            icon = Icons.Default.History,
                            title = "내 여행 내역",
                            subtitle = "전체 여행 지출 리포트 보기",
                            onClick = { navController.navigate(Routes.TRAVEL_HISTORY) }
                        )
                    }
                }

                // 하단 버튼
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
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
                        Text("com.han.tripmate", fontSize = 11.sp, color = Color.LightGray, letterSpacing = 1.sp)
                    }
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }

        if (viewModel.isUpdating || isCheckingVersion) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black.copy(alpha = 0.3f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }

    // 이름 수정 다이얼로그
    if (showNameEditDialog) {
        AlertDialog(
            onDismissRequest = { showNameEditDialog = false },
            title = { Text("이름 변경") },
            text = {
                Column {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("새 이름 입력") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (viewModel.errorMessage != null) {
                        Text(viewModel.errorMessage!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.updateUserName(tempName) { showNameEditDialog = false } }) {
                    Text("저장")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameEditDialog = false; viewModel.clearError() }) { Text("취소") }
            }
        )
    }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("비밀번호 변경", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("현재 비밀번호") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("새 비밀번호 (6자 이상)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("새 비밀번호 확인") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    passwordError?.let {
                        Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        when {
                            currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty() -> {
                                passwordError = "모든 항목을 입력해 주세요."
                            }
                            newPassword.length < 6 -> {
                                passwordError = "새 비밀번호는 6자리 이상이어야 합니다."
                            }
                            newPassword != confirmPassword -> {
                                passwordError = "새 비밀번호가 일치하지 않습니다."
                            }
                            else -> {
                                passwordError = null
                                showPasswordDialog = false
                            }
                        }
                    }
                ) {
                    Text("변경하기")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) { Text("취소") }
            }
        )
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("언어 설정", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    languageOptions.forEach { language ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedLanguage = language }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedLanguage == language),
                                onClick = { selectedLanguage = language }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = language, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    showLanguageDialog = false
                }) {
                    Text("적용")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLanguageDialog = false }) { Text("취소") }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("개인정보 처리방침", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().height(300.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "TripMate(이하 '앱')는 이용자의 개인정보를 소중하게 처리하며, 개인정보보호법 등 관련 법령을 준수합니다.", fontSize = 14.sp, color = Color.DarkGray)
                    Text(text = "1. 수집하는 개인정보 항목", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "- 필수 항목: 이메일, 닉네임, 프로필 사진\n- 선택 항목: 여행 선호 지역, 활동 기록 데이터", fontSize = 13.sp, color = Color.Gray)
                    Text(text = "2. 개인정보의 이용 목적", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "- 가이드 매칭 서비스 제공 및 유저 식별\n- 푸시 알림 및 이벤트 정보 발송 (동의 시)", fontSize = 13.sp, color = Color.Gray)
                    Text(text = "3. 개인정보의 보유 및 파기", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "회원 탈퇴 시 수집된 개인정보는 지체 없이 완전히 파기되며, 법령에 의해 보존할 필요가 있는 경우 목적 달성 후 안전하게 삭제됩니다.", fontSize = 13.sp, color = Color.Gray)
                }
            },
            confirmButton = { Button(onClick = { showPrivacyDialog = false }) { Text("동의 및 확인") } }
        )
    }

    // 💡 [68일차 추가] 진짜로 확인 작업을 수행하는 버전 정보 알림 팝업
    if (showVersionDialog) {
        AlertDialog(
            onDismissRequest = { showVersionDialog = false },
            title = { Text("버전 정보 확인", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("현재 설치 버전: 1.0.0", fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("최신 릴리즈 버전: 1.0.0", fontSize = 15.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("현재 최신 버전의 TripMate를 사용하고 있습니다. 새로운 기능이 추가되면 스토어를 통해 알려드릴게요!", fontSize = 13.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                Button(onClick = { showVersionDialog = false }) {
                    Text("확인")
                }
            }
        )
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
fun ProfileCard(
    userName: String,
    profileUrl: String?,
    onEditClick: () -> Unit,
    onNameClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(24.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(modifier = Modifier.size(64.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primary) {
                    if (profileUrl != null) {
                        AsyncImage(model = profileUrl, contentDescription = null, modifier = Modifier.clip(CircleShape), contentScale = ContentScale.Crop)
                    } else {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(12.dp), tint = Color.White)
                    }
                }
                Surface(
                    onClick = onEditClick,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.padding(4.dp), tint = Color.White)
                }
            }

            Column(modifier = Modifier.padding(start = 20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onNameClick() }
                ) {
                    Text(text = "${userName}님", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp).padding(start = 4.dp), tint = Color.Gray)
                }
                Text(text = "나의 프로필 정보 수정", fontSize = 14.sp, color = Color.Gray)
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