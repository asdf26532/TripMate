package com.han.tripmate.data

import com.google.firebase.auth.FirebaseAuth

class AuthRepository {
    // Firebase 인증 객체 초기화
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    //실제 이메일 회원가입
    fun signUp(email: String, pw: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pw)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 가입 성공
                    onResult(true, null)
                } else {
                    // 가입 실패 (중복 이메일, 비밀번호 취약 등)
                    onResult(false, task.exception?.message ?: "알 수 없는 에러가 발생했습니다.")
                }
            }
    }

    //실제 로그인 함수
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

    // 현재 로그인된 사용자가 있는지 확인
    fun getCurrentUser() = auth.currentUser
}