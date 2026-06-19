package com.han.tripmate.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.han.tripmate.ui.theme.MainBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipGroup(
    items: List<String>,
    selectedItem: String,
    onSelectedChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            val isSelected = item == selectedItem

            FilterChip(
                selected = isSelected,
                onClick = { onSelectedChanged(item) },
                label = { Text(text = item) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MainBlue.copy(alpha = 0.15f),
                    selectedLabelColor = MainBlue,
                    containerColor = Color(0xFFF1F3F5),
                    labelColor = Color.DarkGray
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    selectedBorderColor = MainBlue,
                    borderColor = Color.Transparent
                )
            )
        }
    }
}