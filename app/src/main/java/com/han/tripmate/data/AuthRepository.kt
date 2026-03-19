package com.han.tripmate.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // 이메일 회원가입
    fun signUp(email: String, pw: String, nickname: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pw)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        val userMap = hashMapOf(
                            "uid" to uid,
                            "email" to email,
                            "nickname" to nickname,
                            "createdAt" to com.google.firebase.Timestamp.now(),
                            "role" to "USER"
                        )

                        db.collection("users").document(uid)
                            .set(userMap)
                            .addOnSuccessListener {
                                onResult(true, null)
                            }
                            .addOnFailureListener { e ->
                                onResult(false, e.message ?: "데이터베이스 저장 실패")
                            }
                    }
                } else {
                    onResult(false, task.exception?.message ?: "회원가입 실패")
                }
            }
    }
    // 로그인
    fun signIn(email: String, pw: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, pw)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message ?: "로그인에 실패했습니다.")
                }
            }
    }
    // 로그 아웃
    fun logout() {
        auth.signOut()
    }

    fun getUserDetails(uid: String, onResult: (Map<String, Any>?, String?) -> Unit) {
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    onResult(document.data, null)
                } else {
                    onResult(null, "유저 정보를 찾을 수 없습니다.")
                }
            }
            .addOnFailureListener { e ->
                onResult(null, e.message)
            }
    }

    // 현재 로그인된 사용자가 있는지 확인
    fun getCurrentUser() = auth.currentUser
}