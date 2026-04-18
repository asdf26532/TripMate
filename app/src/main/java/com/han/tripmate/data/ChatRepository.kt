package com.han.tripmate.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.han.tripmate.data.model.Message
import kotlinx.coroutines.tasks.await

class ChatRepository {
    private val db = FirebaseFirestore.getInstance()

    fun observeMessages(roomId: String, onResult: (List<Message>) -> Unit): ListenerRegistration {
        return db.collection("chat_rooms").document(roomId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                val messages = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Message::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                onResult(messages)
            }
    }

    suspend fun sendMessage(roomId: String, message: Message): Boolean {
        return try {
            db.collection("chat_rooms").document(roomId)
                .collection("messages")
                .add(message).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}