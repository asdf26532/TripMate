package com.han.tripmate.ui.viewmodel

import android.net.Uri
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.han.tripmate.data.model.Plan

class PlanViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

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
}