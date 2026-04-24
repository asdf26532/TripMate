package com.han.tripmate.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.han.tripmate.data.model.Plan
import kotlinx.coroutines.tasks.await

class PlanRepository {
    private val db = FirebaseFirestore.getInstance()

    fun observePlans(uid: String, onResult: (List<Plan>) -> Unit) {
        db.collection("plans")
            .whereEqualTo("authorId", uid)
            .orderBy("day", Query.Direction.ASCENDING)
            .orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { it.toObject(Plan::class.java)?.copy(id = it.id) } ?: emptyList()
                onResult(list)
            }
    }

    suspend fun updatePlanDetails(planId: String, updates: Map<String, Any>): Boolean {
        return try {
            db.collection("plans").document(planId).update(updates).await()
            true
        } catch (e: Exception) { false }
    }


}

