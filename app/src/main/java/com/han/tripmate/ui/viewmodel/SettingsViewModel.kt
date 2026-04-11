package com.han.tripmate.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class SettingsViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

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

    fun updateUserName(newName: String, onComplete: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        errorMessage = null

        db.collection("users").document(uid).update("name", newName)
            .addOnSuccessListener {
                userName = newName
                onComplete()
            }
            .addOnFailureListener { e ->
                errorMessage = "이름 업데이트 실패: ${e.localizedMessage}"
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

    // 프로필 이미지 업로드 (Storage -> Firestore -> Auth)
    fun uploadProfileImage(uri: Uri) {
        val uid = auth.currentUser?.uid ?: return
        val ref = storage.reference.child("profiles/$uid.jpg")

        ref.putFile(uri).continueWithTask { task ->
            if (!task.isSuccessful) task.exception?.let { throw it }
            ref.downloadUrl
        }.addOnSuccessListener { downloadUri ->
            // Firestore 업데이트 및 UI 반영
            profileImageUrl = downloadUri.toString()
            db.collection("users").document(uid).update("profileImage", profileImageUrl)

        }
    }
}