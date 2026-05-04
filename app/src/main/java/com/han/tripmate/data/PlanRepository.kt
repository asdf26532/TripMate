package com.han.tripmate.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.han.tripmate.data.model.Itinerary
import com.han.tripmate.data.model.Plan
import kotlinx.coroutines.tasks.await

class PlanRepository {
    private val db = FirebaseFirestore.getInstance()

    fun observePlans(uid: String, onResult: (List<Plan>) -> Unit) {
        db.collection("plans")
            .whereEqualTo("authorId", uid)
            .orderBy("day", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Plan::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                onResult(list)
            }
    }

    suspend fun updatePlanDetails(planId: String, updates: Map<String, Any>): Boolean {
        return try {
            db.collection("plans").document(planId).update(updates).await()
            true
        } catch (e: Exception) { false }
    }

    fun getItineraries(planId: String, onResult: (List<Itinerary>) -> Unit) {
        db.collection("plans")
            .document(planId)
            .collection("itineraries")
            .orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Itinerary::class.java)?.copy(id = doc.id)
                }
                onResult(list)
            }
    }

}

