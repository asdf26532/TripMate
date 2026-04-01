package com.han.tripmate.ui.screens

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.han.tripmate.data.model.Plan
import com.han.tripmate.ui.viewmodel.PlanViewModel

@Composable
fun PlanMapScreen(
    plan: Plan,
    viewModel: PlanViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    var targetLocation by remember { mutableStateOf(LatLng(37.5665, 126.9780)) } // 기본 서울
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(targetLocation, 15f)
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

    //  지오코딩: 주소를 좌표로 변환
    LaunchedEffect(plan.location) {
        val latLng = viewModel.getLatLngFromAddress(context, plan.location)
        if (latLng != null) {
            targetLocation = latLng
            cameraPositionState.animate(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        } else {
            Toast.makeText(context, "장소를 찾을 수 없어 기본 위치로 표시합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = true) // 내 위치 파란 점 활성화
    ) {
        Marker(
            state = MarkerState(position = targetLocation),
            title = plan.title,
            snippet = plan.location
        )
    }
}