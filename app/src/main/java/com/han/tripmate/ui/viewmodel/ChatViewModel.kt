package com.han.tripmate.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.han.tripmate.data.ChatRepository
import com.han.tripmate.data.ImageRepository
import com.han.tripmate.data.model.ChatRoom
import com.han.tripmate.data.model.Message
import com.han.tripmate.ui.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class ChatViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val imageRepository = ImageRepository()
    private val chatRepository = ChatRepository()

    private var chatListener: ListenerRegistration? = null
    private var roomListListener: ListenerRegistration? = null

    private val _chatRooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chatRooms = _chatRooms.asStateFlow()

    // 메시지 리스트 상태
    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> get() = _messages

    private val _uiState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    // 입력창 텍스트 상태
    var messageText by mutableStateOf("")
        private set

    fun updateMessageText(newText: String) {
        messageText = newText
    }

    fun resetUiState() { _uiState.value = UiState.Idle }

    fun observeChatRoomList() {
        val uid = auth.currentUser?.uid ?: return
        roomListListener?.remove()
        roomListListener = chatRepository.observeChatRooms(uid) { rooms ->
            _chatRooms.value = rooms
        }
    }

    fun observeMessages(chatRoomId: String) {
        chatListener?.remove()

        chatListener = chatRepository.observeMessages(chatRoomId) { newMessages ->
            _messages.clear()
            _messages.addAll(newMessages)
        }
    }

    // 메시지 전송
    fun sendMessage(chatRoomId: String) {
        val uid = auth.currentUser?.uid ?: return
        if (messageText.isBlank()) return

        val textToSend = messageText.trim()
        val timestamp = System.currentTimeMillis().toString()
        val newMessage = Message(
            id = UUID.randomUUID().toString(),
            senderId = uid,
            text = textToSend,
            chatRoomId = chatRoomId,
            timestamp = timestamp
        )

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            messageText = ""

            val success = chatRepository.sendMessage(chatRoomId, newMessage)

            if (success) {
                val roomUpdate = mapOf(
                    "lastMessage" to textToSend,
                    "lastTimestamp" to timestamp
                )
                chatRepository.updateChatRoom(chatRoomId, roomUpdate)
                _uiState.value = UiState.Idle
            } else {
                _uiState.value = UiState.Error("메시지 전송 실패")
                messageText = textToSend
            }
        }
    }

    // 이미지 업로드 및 전송
    fun uploadChatImage(chatRoomId: String, uri: Uri) {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val path = "chat_images/${System.currentTimeMillis()}.jpg"
            val downloadUrl = imageRepository.uploadImage(path, uri)

            if (downloadUrl != null) {
                val imageMessage = Message(
                    id = UUID.randomUUID().toString(),
                    senderId = uid,
                    imageUrl = downloadUrl,
                    chatRoomId = chatRoomId,
                    timestamp = System.currentTimeMillis().toString()
                )
                val success = chatRepository.sendMessage(chatRoomId, imageMessage)
                _uiState.value = if (success) UiState.Idle else UiState.Error("이미지 메시지 저장 실패")
            } else {
                _uiState.value = UiState.Error("이미지 업로드 실패")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        roomListListener?.remove()
        chatListener?.remove()
    }

}