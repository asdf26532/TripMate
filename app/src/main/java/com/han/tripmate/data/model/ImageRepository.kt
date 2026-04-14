package com.han.tripmate.data.model

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class ImageRepository {
    private val storage = FirebaseStorage.getInstance()

    // 공통 업로드 함수
    suspend fun uploadImage(path: String, uri: Uri): String? {
        return try {
            val ref = storage.reference.child(path)
            ref.putFile(uri).await() // 업로드 완료 대기
            ref.downloadUrl.await().toString() // URL 다운로드 대기
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}