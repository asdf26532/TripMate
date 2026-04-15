package com.han.tripmate.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun updateProfile(uid: String, updates: Map<String, Any>): Boolean {
        return try {
            db.collection("users").document(uid).update(updates).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}