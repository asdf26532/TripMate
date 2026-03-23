package com.han.tripmate.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.han.tripmate.data.model.Message
import java.text.SimpleDateFormat
import java.util.*

class ChatViewModel : ViewModel() {

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

    fun observeMessages(chatRoomId: String) {
        db.collection("chat_rooms").document(chatRoomId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                if (snapshot != null) {
                    val newMessages = snapshot.toObjects(Message::class.java)
                    messages.clear()
                    messages.addAll(newMessages)
                }
            }
    }

    fun sendMessage() {
        if (messageText.isNotBlank()) {
            val newMessage = Message(
                id = UUID.randomUUID().toString(),
                senderId = "my_id", // 현재 로그인 유저
                text = messageText,
                timestamp = SimpleDateFormat("HH:mm", Locale.KOREA).format(Date())
            )
            _messages.add(newMessage)
            messageText = "" // 입력창 초기화
        }
    }
}