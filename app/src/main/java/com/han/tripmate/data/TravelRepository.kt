package com.han.tripmate.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class TravelRepository {
    private val db = FirebaseFirestore.getInstance()

    // 실시간 서비스 목록
    fun observeServices(onResult: (List<TravelService>) -> Unit): ListenerRegistration {
        return db.collection("travel_services")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(TravelService::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                onResult(list)
            }
    }

    // 서비스 등록
    fun addService(service: TravelService, onComplete: (Boolean) -> Unit) {
        db.collection("travel_services").add(service)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}