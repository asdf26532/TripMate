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

            // Firebase Auth 프로필 정보 업데이트 (선택 사항)
            val profileUpdates = com.google.firebase.auth.userProfileChangeRequest.Builder()
                .setPhotoUri(downloadUri)
                .build()
            auth.currentUser?.updateProfile(profileUpdates)
        }
    }
}