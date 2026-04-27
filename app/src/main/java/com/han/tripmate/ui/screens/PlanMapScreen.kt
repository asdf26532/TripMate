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
import androidx.compose.material.icons.filled.Directions
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
import com.han.tripmate.ui.util.UiState
import com.han.tripmate.ui.viewmodel.PlanViewModel

@Composable
fun PlanMapScreen(
    currentPlan: Plan,
    allPlans: List<Plan>,
    viewModel: PlanViewModel = viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var targetLocation by remember { mutableStateOf(LatLng(37.5665, 126.9780)) } // 기본 서울
    var isLocationChanged by remember { mutableStateOf(false) }

    val pathPoints = viewModel.allPlanLocations
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(targetLocation, 15f)
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> {
                val message = (uiState as UiState.Success<String>).data
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.resetUiState() // [수정] clearUpdateResult 대신 resetUiState 호출
                if (message.contains("수정") || message.contains("성공")) {
                    isLocationChanged = false
                    onBack()
                }
            }
            is UiState.Error -> {
                Toast.makeText(context, (uiState as UiState.Error).message, Toast.LENGTH_SHORT).show()
                viewModel.resetUiState()
            }
            else -> {}
        }
    }

    LaunchedEffect(currentPlan) {
        viewModel.getLatLngFromAddress(context, currentPlan.location)?.let {
            targetLocation = it
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
        }
    }

    LaunchedEffect(allPlans) {
        viewModel.loadAllPlanCoordinates(context, allPlans)
    }

    LaunchedEffect(pathPoints, targetLocation, isLocationChanged) {
        if (isLocationChanged) {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(targetLocation, 16f))
        } else if (pathPoints.isNotEmpty()) {
            val bounds = viewModel.calculateBounds(pathPoints)
            bounds?.let {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(it, 150))
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
                val isSelected = allPlans[index].id == currentPlan.id
                Marker(
                    state = MarkerState(position = latLng),
                    title = allPlans[index].title,
                    snippet = allPlans[index].location,
                    icon = BitmapDescriptorFactory.defaultMarker(
                        if (isSelected) BitmapDescriptorFactory.HUE_RED else BitmapDescriptorFactory.HUE_AZURE
                    ),
                    zIndex = if (isSelected) 1f else 0f
                )
            }

            if (isLocationChanged) {
                Marker(
                    state = MarkerState(position = targetLocation),
                    title = "변경할 위치",
                    snippet = searchQuery,
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
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
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.TopCenter),
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
                                    isLocationChanged = true
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
                Button(
                    onClick = {
                        viewModel.updatePlanLocation(currentPlan.id, searchQuery)
                    },
                    modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally)
                ) {
                    Text("이 장소로 확정")
                }
            }
        }

        if (uiState is UiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        ExtendedFloatingActionButton(
            onClick = {
                viewModel.openExternalMap(
                    context = context,
                    label = if (isLocationChanged) "검색된 위치" else currentPlan.title,
                    lat = targetLocation.latitude,
                    lng = targetLocation.longitude
                )
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 32.dp, end = 16.dp),
            containerColor = Color(0xFF007AFF),
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Icon(Icons.Default.Directions, contentDescription = "길찾기")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "길찾기", fontWeight = FontWeight.Bold)
        }
    }
}