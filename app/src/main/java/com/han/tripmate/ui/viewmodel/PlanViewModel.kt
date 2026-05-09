package com.han.tripmate.ui.viewmodel

import android.net.Uri
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.han.tripmate.data.ImageRepository
import com.han.tripmate.data.PlanRepository
import com.han.tripmate.data.model.Plan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import com.han.tripmate.ui.util.UiState
import kotlinx.coroutines.tasks.await
import android.util.Log
import com.han.tripmate.data.model.Itinerary

class PlanViewModel : ViewModel() {

    private val planRepository = PlanRepository()
    private val imageRepository = ImageRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val uiState: StateFlow<UiState<String>> = _uiState.asStateFlow()

    private val _plans = MutableStateFlow<List<Plan>>(emptyList())
    val plans: StateFlow<List<Plan>> = _plans.asStateFlow()

    private val _allPlanLocations = mutableStateListOf<LatLng>()
    val allPlanLocations: List<LatLng> = _allPlanLocations

    val groupedPlans: StateFlow<Map<Int, List<Plan>>> = _plans.map { list ->
        list.groupBy { it.day }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    val totalExpense: StateFlow<Int> = _plans.map { list ->
        list.sumOf { it.expense }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    private val _itineraryList = MutableStateFlow<List<Itinerary>>(emptyList())
    val itineraryList: StateFlow<List<Itinerary>> = _itineraryList.asStateFlow()

    init {
        observeUserPlans()
    }

    private fun observeUserPlans() {
        val uid = auth.currentUser?.uid ?: return
        planRepository.observePlans(uid) { planList ->
            _plans.value = planList
        }
    }

    fun resetUiState() { _uiState.value = UiState.Idle }

    fun addPlan(plan: Plan) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val newPlan = plan.copy(authorId = uid)
                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("plans")
                    .add(newPlan).await()
                _uiState.value = UiState.Success("새 일정이 추가되었습니다.")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("일정 추가 실패")
            }
        }
    }

    fun uploadPlanImage(planId: String, imageUri: Uri) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val path = "plans/$planId/${System.currentTimeMillis()}.jpg"
            val downloadUrl = imageRepository.uploadImage(path, imageUri)

            if (downloadUrl != null) {
                val success = planRepository.updatePlanDetails(
                    planId,
                    mapOf("imageUrls" to FieldValue.arrayUnion(downloadUrl))
                )
                if (success) {
                    _uiState.value = UiState.Success("인증샷이 성공적으로 업로드되었습니다.")
                } else {
                    _uiState.value = UiState.Error("데이터 업데이트 실패")
                }
            } else {
                _uiState.value = UiState.Error("이미지 업로드 실패")
            }
        }
    }

    fun updatePlanLocation(planId: String, newLocation: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val success = planRepository.updatePlanDetails(planId, mapOf("location" to newLocation))
            if (success) {
                _uiState.value = UiState.Success("장소가 수정되었습니다.")
            } else {
                _uiState.value = UiState.Error("장소 수정 실패")
            }
        }
    }

    fun updatePlanDetails(planId: String, memo: String, expense: Int) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val success = planRepository.updatePlanDetails(
                planId,
                mapOf("memo" to memo, "expense" to expense)
            )
            if (success) {
                _uiState.value = UiState.Success("기록이 저장되었습니다.")
            } else {
                _uiState.value = UiState.Error("저장에 실패했습니다.")
            }
        }
    }

    fun searchLocation(context: Context, query: String, onResult: (LatLng?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val latLng = getLatLngFromAddress(context, query)
            withContext(Dispatchers.Main) {
                onResult(latLng)
            }
        }
    }

    suspend fun getLatLngFromAddress(context: Context, address: String): LatLng? = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.KOREA)
            val addresses = geocoder.getFromLocationName(address, 1)
            if (!addresses.isNullOrEmpty()) {
                LatLng(addresses[0].latitude, addresses[0].longitude)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun loadAllPlanCoordinates(context: Context, plans: List<Plan>) {
        viewModelScope.launch(Dispatchers.IO) {
            val coordinates = plans.mapNotNull { plan ->
                getLatLngFromAddress(context, plan.location)
            }
            withContext(Dispatchers.Main) {
                _allPlanLocations.clear()
                _allPlanLocations.addAll(coordinates)
            }
        }
    }

    fun calculateBounds(locations: List<LatLng>): LatLngBounds? {
        if (locations.isEmpty()) return null
        val builder = LatLngBounds.Builder()
        locations.forEach { builder.include(it) }
        return builder.build()
    }

    fun openExternalMap(context: Context, label: String, lat: Double, lng: Double) {
        val uriString = "geo:$lat,$lng?q=$lat,$lng($label)"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString))

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
            val webIntent = Intent(Intent.ACTION_VIEW, webUri)
            context.startActivity(webIntent)
        }
    }

    fun sortItinerariesByTime() {
        viewModelScope.launch {
            try {
                val sorted = _itineraryList.value.sortedBy { it.time }
                _itineraryList.value = sorted
            } catch (e: Exception) {
                Log.e("PlanViewModel", "정렬 오류: ${e.message}")
            }
        }
    }

    fun loadItineraries(planId: String) {
        viewModelScope.launch {

            planRepository.getItineraries(planId) { list ->
                _itineraryList.value = list
                sortItinerariesByTime()
            }
        }
    }

    fun getDistanceString(start: Itinerary, end: Itinerary): String {
        val startLoc = android.location.Location("start").apply {
        }
        val endLoc = android.location.Location("end").apply {
            latitude = end.lat
            longitude = end.lng
        }

        val distanceInMeters = startLoc.distanceTo(endLoc)
        return if (distanceInMeters >= 1000) {
            String.format("%.1fkm", distanceInMeters / 1000)
        } else {
            "${distanceInMeters.toInt()}m"
        }
    }

    fun estimateTravelTime(start: Itinerary, end: Itinerary): String {
        val startLoc = android.location.Location("start").apply {
            latitude = start.lat
            longitude = start.lng
        }
        val endLoc = android.location.Location("end").apply {
            latitude = end.lat
            longitude = end.lng
        }

        val meters = startLoc.distanceTo(endLoc)
        val minutes = (meters / 666).toInt()

        return if (minutes < 1) "1분 내외" else "${minutes}분"
    }

    fun addItineraryWithLocation(context: Context, planId: String, title: String, time: String, memo: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val latLng = getLatLngFromAddress(context, title)

            val newItinerary = Itinerary(
                title = title,
                time = time,
                memo = memo,
                lat = latLng?.latitude ?: 0.0,
                lng = latLng?.longitude ?: 0.0
            )

            val success = planRepository.addItinerary(planId, newItinerary)
            if (success) {
                loadItineraries(planId)
                _uiState.value = UiState.Success("일정이 추가되었습니다.")
            } else {
                _uiState.value = UiState.Error("저장 실패")
            }
        }
    }

    fun deleteItinerary(planId: String, itineraryId: String) {
        viewModelScope.launch {
            val success = planRepository.deleteItinerary(planId, itineraryId)
            if (success) {
                loadItineraries(planId)
            }
        }
    }

}