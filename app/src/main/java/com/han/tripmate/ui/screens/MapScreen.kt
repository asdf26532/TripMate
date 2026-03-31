package com.han.tripmate.ui.screens

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
    var targetLocation by remember { mutableStateOf(LatLng(35.115, 129.042)) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(targetLocation, 15f)
    }

    LaunchedEffect(plan.location) {
        val latLng = viewModel.getLatLngFromAddress(context, plan.location)
        if (latLng != null) {
            targetLocation = latLng
            // 카메라 이동
            cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 16f)
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // 마커
        Marker(
            state = MarkerState(position = targetLocation),
            title = plan.title,
            snippet = plan.location
        )
    }
}