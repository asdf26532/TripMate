package com.han.tripmate.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.han.tripmate.data.ImageRepository
import com.han.tripmate.data.UserRepository
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val imageRepository = ImageRepository()
    private val userRepository = UserRepository()

    var profileImageUrl by mutableStateOf(auth.currentUser?.photoUrl?.toString())
        private set

    var userName by mutableStateOf(auth.currentUser?.displayName ?: "여행자")
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isNotificationsEnabled by mutableStateOf(true)
        private set

    var isDarkMode by mutableStateOf(false)
        private set

    var isUpdating by mutableStateOf(false)
        private set

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                errorMessage = "데이터를 불러오는데 실패했습니다."
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                snapshot.getString("name")?.let { userName = it }
                snapshot.getString("profileImage")?.let { profileImageUrl = it }
            }
        }
    }

    fun clearError() { errorMessage = null }

    fun toggleNotifications(enabled: Boolean) {
        isNotificationsEnabled = enabled
    }

    fun toggleDarkMode(enabled: Boolean) {
        isDarkMode = enabled
    }

    // 로그아웃
    fun signOut(onComplete: () -> Unit) {
        auth.signOut()
        onComplete()
    }

    // 회원 탈퇴
    fun deleteAccount(onComplete: () -> Unit) {
        val user = auth.currentUser
        user?.delete()?.addOnCompleteListener {
            if (it.isSuccessful) onComplete()
        }
    }

    // 프로필 이미지
    fun uploadProfileImage(uri: Uri) {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            isUpdating = true
            val path = "profiles/$uid.jpg"
            val downloadUrl = imageRepository.uploadImage(path, uri)

            if (downloadUrl != null) {
                val success = userRepository.updateProfile(uid, mapOf("profileImage" to downloadUrl))
                if (success) profileImageUrl = downloadUrl
            }
            isUpdating = false
        }
    }

    fun updateUserName(newName: String, onComplete: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            isUpdating = true
            val success = userRepository.updateProfile(uid, mapOf("name" to newName))
            if (success) {
                userName = newName
                onComplete()
            }
            isUpdating = false
        }
    }

}