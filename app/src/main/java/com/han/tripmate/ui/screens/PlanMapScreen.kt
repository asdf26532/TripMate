package com.han.tripmate.ui.screens

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.han.tripmate.data.model.Plan
import com.han.tripmate.ui.viewmodel.PlanViewModel

@Composable
fun PlanMapScreen(
    currentPlan: Plan,
    allPlans: List<Plan>,
    viewModel: PlanViewModel = viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var targetLocation by remember { mutableStateOf(LatLng(37.5665, 126.9780)) } // 기본 서울
    var isLocationChanged by remember { mutableStateOf(false) }

    val pathPoints = viewModel.allPlanLocations
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(targetLocation, 15f)
    }

    LaunchedEffect(allPlans) {
        viewModel.loadAllPlanCoordinates(context, allPlans)
    }

    LaunchedEffect(currentPlan.location) {
        val latLng = viewModel.getLatLngFromAddress(context, currentPlan.location)
        if (latLng != null) {
            targetLocation = latLng
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        }
    }

    val updateMessage by viewModel.updateResult
    LaunchedEffect(updateMessage) {
        updateMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearUpdateResult()
            if (it.contains("성공")) {
                isLocationChanged = false
                onBack()
            }
        }
    }

    // 위치 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            Toast.makeText(context, "정밀 위치 권한 허용됨", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(myLocationButtonEnabled = true)
        ) {
            pathPoints.forEachIndexed { index, latLng ->
                Marker(
                    state = MarkerState(position = latLng),
                    title = allPlans.getOrNull(index)?.title ?: "장소",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
            }

            if (pathPoints.size >= 2) {
                Polyline(
                    points = pathPoints,
                    color = Color(0xFF007AFF),
                    width = 12f,
                    geodesic = true
                )
            }

            Marker(
                state = MarkerState(position = targetLocation),
                title = if (!isLocationChanged) currentPlan.title else "새로운 위치",
                snippet = if (!isLocationChanged) currentPlan.location else searchQuery
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("어디로 가고 싶으신가요?") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    IconButton(onClick = {
                        if (searchQuery.isNotBlank()) {
                            viewModel.searchLocation(context, searchQuery) { resultLatLng ->
                                if (resultLatLng != null) {
                                    targetLocation = resultLatLng
                                    isLocationChanged = true // [수정] 여기서 true로 바꿔줘야 버튼이 나타납니다.
                                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(resultLatLng, 16f))
                                } else {
                                    Toast.makeText(context, "장소를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF007AFF))
                    }
                }
            }

            if (isLocationChanged) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.updatePlanLocation(context, currentPlan.id, searchQuery) },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("이 위치로 일정 수정", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}