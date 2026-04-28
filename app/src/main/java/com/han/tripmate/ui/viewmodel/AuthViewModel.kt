package com.han.tripmate.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.han.tripmate.data.AuthRepository
import com.han.tripmate.data.UserPreferences
import com.han.tripmate.data.model.User
import com.han.tripmate.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userPrefs = UserPreferences(application)
    private val authRepository = AuthRepository()

    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var nickname by mutableStateOf("")
        private set

    var emailError by mutableStateOf<String?>(null)
        private set
    var passwordError by mutableStateOf<String?>(null)
        private set
    var nicknameError by mutableStateOf<String?>(null)
        private set


    val isLoginValid: Boolean
        get() = email.isNotBlank() && password.isNotBlank() && emailError == null && passwordError == null

    val isSignUpValid: Boolean
        get() = isLoginValid && nickname.isNotBlank() && nicknameError == null


    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkSavedUser()
    }

    // --- 입력 로직 ---
    fun onEmailChanged(newValue: String) {
        email = newValue
        emailError = when {
            newValue.isBlank() -> "이메일을 입력해 주세요."
            !android.util.Patterns.EMAIL_ADDRESS.matcher(newValue).matches() -> "올바른 이메일 형식이 아닙니다."
            else -> null
        }
    }

    fun onPasswordChanged(newValue: String) {
        password = newValue
        passwordError = when {
            newValue.isBlank() -> "비밀번호를 입력해 주세요."
            newValue.length < 6 -> "비밀번호는 최소 6자 이상이어야 합니다."
            else -> null
        }
    }

    fun onNicknameChanged(newValue: String) {
        nickname = newValue
        nicknameError = if (newValue.isBlank()) "닉네임을 입력해 주세요." else null
    }

    private fun checkSavedUser() {
        val firebaseUser = authRepository.getCurrentUser()
        if (firebaseUser != null) {
            _currentUser.value = User(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                nickname = firebaseUser.displayName ?: "",
                isVerified = true
            )
            authRepository.getUserDetails(firebaseUser.uid) { data, _ ->
                if (data != null) {
                    val nicknameDetail = data["nickname"] as? String ?: "여행자"
                    val roleStr = data["role"] as? String ?: "USER"
                    val role = if (roleStr == "GUIDE") UserRole.GUIDE else UserRole.USER

                    _currentUser.value = _currentUser.value?.copy(
                        nickname = nicknameDetail,
                        currentRole = role
                    )
                }
            }
        }
    }

    fun updateProfile(newNickname: String, bio: String, styles: List<String>) {
        val uid = _currentUser.value?.id ?: return
        _isLoading.value = true

        val updates = mapOf(
            "nickname" to newNickname,
            "bio" to bio,
            "travelStyles" to styles
        )

        authRepository.updateUserDetails(uid, updates) { success, error ->
            _isLoading.value = false
            if (success) {
                _currentUser.value = _currentUser.value?.copy(
                    nickname = newNickname,
                    bio = bio,
                    travelStyles = styles
                )
                _errorMessage.value = "프로필이 성공적으로 업데이트되었습니다."
            } else {
                _errorMessage.value = error ?: "업데이트에 실패했습니다."
            }
        }
    }

    fun signUp() {
        if (!isSignUpValid) return
        _isLoading.value = true
        _errorMessage.value = null

        authRepository.signUp(email, password, nickname) { success, error ->
            _isLoading.value = false
            if (success) {
                checkSavedUser()
            } else {
                _errorMessage.value = error ?: "회원가입에 실패했습니다."
            }
        }
    }

    fun login() {
        if (!isLoginValid) return
        _isLoading.value = true
        _errorMessage.value = null

        authRepository.signIn(email, password) { success, _ ->
            _isLoading.value = false
            if (success) {
                checkSavedUser()
            } else {
                _errorMessage.value = "이메일 또는 비밀번호가 올바르지 않습니다."
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _currentUser.value = null
        viewModelScope.launch {
            userPrefs.clear()
        }
    }



    fun clearError() {
        _errorMessage.value = null
    }
}