package com.han.tripmate.ui.viewmodel

import android.net.Uri
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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

class PlanViewModel : ViewModel() {

    private val planRepository = PlanRepository()
    private val imageRepository = ImageRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _updateResult = mutableStateOf<String?>(null)
    val updateResult: State<String?> = _updateResult

    private val _plans = MutableStateFlow<List<Plan>>(emptyList())
    val plans: StateFlow<List<Plan>> = _plans.asStateFlow()

    private val _allPlanLocations = mutableStateListOf<LatLng>()
    val allPlanLocations: List<LatLng> = _allPlanLocations

    init {
        observeUserPlans()
    }

    private fun observeUserPlans() {
        val uid = auth.currentUser?.uid ?: return
        planRepository.observePlans(uid) { planList ->
            _plans.value = planList
        }
    }

    fun addPlan(plan: Plan) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val newPlan = plan.copy(authorId = uid)
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("plans")
                .add(newPlan)
        }
    }

    fun uploadPlanImage(context: Context, planId: String, imageUri: Uri) {
        viewModelScope.launch {
            val path = "plans/$planId/${System.currentTimeMillis()}.jpg"
            val downloadUrl = imageRepository.uploadImage(path, imageUri)

            if (downloadUrl != null) {
                val success = planRepository.updatePlanDetails(
                    planId,
                    mapOf("imageUrls" to FieldValue.arrayUnion(downloadUrl))
                )
                if (success) {
                    Toast.makeText(context, "인증샷이 업로드되었습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updatePlanLocation(context: Context, planId: String, newLocation: String) {
        viewModelScope.launch {
            val success = planRepository.updatePlanDetails(planId, mapOf("location" to newLocation))
            if (success) {
                _updateResult.value = "장소가 성공적으로 수정되었습니다."
            } else {
                _updateResult.value = "수정 실패"
            }
        }
    }

    fun updatePlanDetails(planId: String, memo: String, expense: Int) {
        viewModelScope.launch {
            val success = planRepository.updatePlanDetails(
                planId,
                mapOf("memo" to memo, "expense" to expense)
            )
            _updateResult.value = if (success) "기록이 저장되었습니다." else "저장에 실패했습니다."
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

    fun clearUpdateResult() { _updateResult.value = null }

}