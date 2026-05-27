package com.han.tripmate.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.horizontalScroll

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.han.tripmate.data.model.PackingItem
import com.han.tripmate.ui.theme.MainBlue
import com.han.tripmate.ui.viewmodel.TravelPackingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelPackingScreen(
    onBack: () -> Unit = {},
    packingViewModel: TravelPackingViewModel = viewModel()
) {

    val packingList by packingViewModel.packingList.collectAsState()

    var newItemName by remember { mutableStateOf("") }
    val selectedCategory by remember { mutableStateOf("필수품") }
    var selectedTemplate by remember { mutableStateOf("기본") }

    var isPackedSectionExpanded by remember { mutableStateOf(false) }

    val templates = mapOf(
        "기본" to listOf(),
        "🏖️ 휴양지/물놀이" to listOf(
            PackingItem("t1", "수영복 및 래시가드", "의류"),
            PackingItem("t2", "방수팩", "필수품"),
            PackingItem("t3", "선글라스", "필수품"),
            PackingItem("t4", "비치타월", "기타")
        ),
        "🏂 겨울/스키장" to listOf(
            PackingItem("t5", "핫팩/방한용품", "필수품"),
            PackingItem("t6", "목도리 및 장갑", "의류"),
            PackingItem("t7", "보습 크림/립밤", "세면/화장품")
        ),
        "💼 비즈니스/출장" to listOf(
            PackingItem("t8", "셔츠 및 정장", "의류"),
            PackingItem("t9", "노트북 및 충전기", "전자기기"),
            PackingItem("t10", "명함", "필수품")
        )
    )

    val packedCount = packingList.count { it.isPacked }
    val totalCount = packingList.size
    val progress = if (totalCount > 0) packedCount.toFloat() / totalCount else 0f

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("짐 싸기 체크리스트", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("나의 짐 싸기 진척도", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            Text("$packedCount / $totalCount", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                            color = MainBlue,
                            trackColor = MainBlue.copy(alpha = 0.1f)
                        )
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "추천 템플릿 추가",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        templates.keys.forEach { theme ->
                            val isSelected = selectedTemplate == theme
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedTemplate = theme

                                    val themeItems = templates[theme] ?: listOf()
                                    packingViewModel.loadTemplate(themeItems)
                                },
                                label = { Text(theme, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MainBlue.copy(alpha = 0.15f),
                                    selectedLabelColor = MainBlue
                                )
                            )
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newItemName,
                            onValueChange = { newItemName = it },
                            placeholder = { Text("예: 면도기, 선글라스", fontSize = 14.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newItemName.isNotBlank()) {
                                    packingViewModel.addItem(newItemName, selectedCategory)
                                    newItemName = ""
                                }
                            },
                            modifier = Modifier.height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MainBlue)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "추가")
                        }
                    }
                }
            }

            val unpackedItems = packingList.filter { !it.isPacked }
            val groupedUnpacked = unpackedItems.groupBy { it.category }

            groupedUnpacked.forEach { (category, items) ->
                item {
                    Text(
                        text = category,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                items(items, key = { it.id }) { item ->
                    PackingItemRow(
                        item = item,
                        onToggle = { packingViewModel.toggleItemPacked(item.id) },
                        onDelete = { packingViewModel.deleteItem(item.id) }
                    )
                }
            }

            val packedItems = packingList.filter { it.isPacked }

            if (packedItems.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isPackedSectionExpanded = !isPackedSectionExpanded }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "체크한 항목 (${packedItems.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MainBlue
                        )
                        Text(
                            text = if (isPackedSectionExpanded) "접기 ▲" else "펼치기 ▼",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                }

                if (isPackedSectionExpanded) {
                    items(packedItems, key = { it.id }) { item ->
                        PackingItemRow(
                            item = item,
                            onToggle = { packingViewModel.toggleItemPacked(item.id) },
                            onDelete = { packingViewModel.deleteItem(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PackingItemRow(
    item: PackingItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (item.isPacked) Color.Gray.copy(alpha = 0.05f) else Color.White,
        label = "cardBg"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (item.isPacked) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                    contentDescription = "체크",
                    tint = if (item.isPacked) MainBlue else Color.Gray
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = item.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (item.isPacked) Color.Gray else Color.DarkGray,
                    textDecoration = if (item.isPacked) TextDecoration.LineThrough else TextDecoration.None
                )
            }

            IconButton(
                onClick = { onDelete() },
                modifier = Modifier.size(24.dp)
            ) {
                Text(
                    text = "✕",
                    fontSize = 14.sp,
                    color = Color.Gray.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}