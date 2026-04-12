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
            authRepository.getUserDetails(firebaseUser.uid) { data, error ->
                if (data != null) {
                    val nickname = data["nickname"] as? String ?: "여행자"
                    val roleStr = data["role"] as? String ?: "USER"

                    val role = if (roleStr == "GUIDE") UserRole.GUIDE else UserRole.USER

                    _currentUser.value = _currentUser.value?.copy(
                        nickname = nickname,
                        currentRole = role
                    )
                }
            }
        }
    }

    fun signUp(email: String, pw: String, nickname: String) {
        _isLoading.value = true
        _errorMessage.value = null

        authRepository.signUp(email, pw, nickname) { success, error ->
            _isLoading.value = false
            if (success) {
                checkSavedUser()
            } else {
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
        val uid = _currentUser.value?.id ?: return
        _isLoading.value = true

        authRepository.updateUserRole(uid, isGuide) { success ->
            _isLoading.value = false
            if (success) {
                val updatedRole = if (isGuide) UserRole.GUIDE else UserRole.USER
                _currentUser.value = _currentUser.value?.copy(currentRole = updatedRole)
            } else {
                _errorMessage.value = "모드 전환에 실패했습니다. 다시 시도해주세요."
            }
        }
    }

    // 에러 메세지 초기화
    fun clearError() {
        _errorMessage.value = null
    }
}