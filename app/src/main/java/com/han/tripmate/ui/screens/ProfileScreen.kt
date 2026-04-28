package com.han.tripmate.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.han.tripmate.ui.theme.MainBlue
import com.han.tripmate.ui.util.LoadingOverlay
import com.han.tripmate.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileEditSection(authViewModel: AuthViewModel) {
    val allStyles = listOf("가성비", "럭셔리", "액티비티", "휴양", "맛집탐방", "관광중심")
    var selectedStyles by remember { mutableStateOf(authViewModel.currentUser.value?.travelStyles ?: emptyList()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("내 여행 스타일", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            allStyles.forEach { style ->
                val isSelected = selectedStyles.contains(style)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedStyles = if (isSelected) selectedStyles - style else selectedStyles + style
                    },
                    label = { Text(style) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MainBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}