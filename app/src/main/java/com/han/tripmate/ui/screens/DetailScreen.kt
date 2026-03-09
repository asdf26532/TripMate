package com.han.tripmate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.han.tripmate.ui.theme.TripMateTheme
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(serviceId: String, onBack: () -> Unit) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("서비스 상세") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Text(text = "선택하신 서비스 ID는 $serviceId 입니다.", modifier = Modifier.padding(16.dp))


        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    TripMateTheme {
        DetailScreen(
            serviceId = "1",
            onBack = {}
        )
    }
}