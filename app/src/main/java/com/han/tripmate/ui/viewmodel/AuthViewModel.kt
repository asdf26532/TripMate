package com.han.tripmate.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.han.tripmate.data.AuthRepository
import com.han.tripmate.data.UserPreferences
import com.han.tripmate.data.model.User
import com.han.tripmate.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userPrefs = UserPreferences(application)
    private val authRepository = AuthRepository()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        checkSavedUser()
    }

    private fun checkSavedUser() {
        // Firebase에 로그인 세션이 남아있는지 확인
        val firebaseUser = authRepository.getCurrentUser()
        if (firebaseUser != null) {
            _currentUser.value = User(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                nickname = firebaseUser.displayName ?: "",
                isVerified = true
            )
        }
    }

    fun signUp(email: String, pw: String, nickname: String) {
        _isLoading.value = true
        authRepository.signUp(email, pw) { success, error ->
            _isLoading.value = false
            if (success) {
                val firebaseUser = authRepository.getCurrentUser()
                val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                    displayName = nickname
                }
                firebaseUser?.updateProfile(profileUpdates)?.addOnCompleteListener {
                    _isLoading.value = false
                    checkSavedUser()
                }
            } else {
                _isLoading.value = false
                _errorMessage.value = error ?: "회원가입에 실패했습니다."
            }
        }
    }

    fun login(email: String, pw: String) {
        _isLoading.value = true
        _errorMessage.value = null

        authRepository.signIn(email, pw) { success, error ->
            _isLoading.value = false
            if (success) {
                checkSavedUser()
            } else {
                _errorMessage.value = "이메일 또는 비밀번호가 올바르지 않습니다."
            }
        }
    }

    // 로그아웃
    fun logout() {
        authRepository.logout()
        _currentUser.value = null
        viewModelScope.launch {
            userPrefs.clear()
        }
    }

    // 모드 토글
    fun toggleRole(isGuide: Boolean) {
        val current = _currentUser.value ?: return
        val newRole = if (isGuide) UserRole.GUIDE else UserRole.USER
        _currentUser.value = current.copy(currentRole = newRole)
    }

    // 에러 메세지 초기화
    fun clearError() {
        _errorMessage.value = null
    }
}