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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.han.tripmate.data.model.Plan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class PlanViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _updateResult = mutableStateOf<String?>(null)
    val updateResult: State<String?> = _updateResult

    fun clearUpdateResult() { _updateResult.value = null }

    private val _allPlanLocations = mutableStateListOf<LatLng>()
    val allPlanLocations: List<LatLng> = _allPlanLocations

    // 임시 데이터
    private val _plans = mutableStateListOf(
        Plan(title = "부산 도착", date = "2024-03-24", time = "10:00", location = "부산역"),
        Plan(title = "점심 식사", date = "2024-03-24", time = "12:30", location = "본전돼지국밥")
    )
    val plans: List<Plan> = _plans

    fun addPlan(plan: Plan) {
        _plans.add(plan)
    }

    fun removePlan(plan: Plan) {
        _plans.remove(plan)
    }

    fun uploadPlanImage(context: Context, planId: String, imageUri: Uri) {
        val storageRef = storage.reference.child("plans/$planId/${System.currentTimeMillis()}.jpg")

        // 파일 업로드
        storageRef.putFile(imageUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                storageRef.downloadUrl
            }
            .addOnSuccessListener { downloadUrl ->
                // 업로드 성공 시 Firestore의 해당 일정 문서 업데이트
                // imageUrls 리스트에 새로운 URL 추가
                db.collection("plans").document(planId)
                    .update("imageUrls", com.google.firebase.firestore.FieldValue.arrayUnion(downloadUrl.toString()))
                    .addOnSuccessListener {
                        Toast.makeText(context, "인증샷이 성공적으로 업로드되었습니다", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "데이터 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(context, "사진 업로드 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    fun updatePlanLocation(context: Context, planId: String, newLocation: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.collection("plans").document(planId)
                .update("location", newLocation)
                .addOnSuccessListener {
                    _updateResult.value = "장소가 성공적으로 수정되었습니다."
                }
                .addOnFailureListener { e ->
                    _updateResult.value = "수정 실패: ${e.message}"
                }
        }
    }

    suspend fun getLatLngFromAddress(context: Context, address: String): LatLng? = withContext(Dispatchers.IO) {
        return@withContext try {
            val geocoder = Geocoder(context, Locale.KOREA)
            val addresses = geocoder.getFromLocationName(address, 1)

            if (!addresses.isNullOrEmpty()) {
                val location = addresses[0]
                LatLng(location.latitude, location.longitude)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
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

}