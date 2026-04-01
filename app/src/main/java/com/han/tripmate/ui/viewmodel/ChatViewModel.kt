package com.han.tripmate.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.han.tripmate.data.model.Message
import java.text.SimpleDateFormat
import java.util.*

class ChatViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // 메시지 리스트 상태
    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> get() = _messages

    // 입력창 텍스트 상태
    var messageText by mutableStateOf("")
        private set

    fun updateMessageText(newText: String) {
        messageText = newText
    }

    // 실시간 메시지 관찰
    fun observeMessages(chatRoomId: String) {
        db.collection("chats")
            .whereEqualTo("chatRoomId", chatRoomId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                snapshot?.let {
                    _messages.clear()
                    val newMessages = it.toObjects(Message::class.java)
                    _messages.addAll(newMessages)
                }
            }
    }

    // 메시지 전송
    fun sendMessage(chatRoomId: String) {
        val currentUser = auth.currentUser
        val uid = currentUser?.uid ?: return

        if (messageText.isNotBlank()) {
            val newMessage = Message(
                id = UUID.randomUUID().toString(),
                senderId = uid,
                text = messageText.trim(),
                imageUrl = null,
                timestamp = System.currentTimeMillis().toString()
            )
            db.collection("chats").document(newMessage.id).set(newMessage)
            messageText = ""
        }
    }

    // 이미지 업로드 및 전송
    fun uploadChatImage(context: Context, uri: Uri) {
        val uid = auth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("chat_images/${System.currentTimeMillis()}.jpg")

        Toast.makeText(context, "이미지를 전송 중입니다...", Toast.LENGTH_SHORT).show()

        storageRef.putFile(uri).continueWithTask { task ->
            if (!task.isSuccessful) task.exception?.let { throw it }
            storageRef.downloadUrl
        }.addOnSuccessListener { downloadUrl ->
            val imageMessage = Message(
                id = UUID.randomUUID().toString(),
                senderId = uid,
                text = "",
                imageUrl = downloadUrl.toString(),
                timestamp = System.currentTimeMillis().toString()
            )
            db.collection("chats").document(imageMessage.id).set(imageMessage)
        }.addOnFailureListener {
            Toast.makeText(context, "이미지 전송 실패", Toast.LENGTH_SHORT).show()
        }
    }
}