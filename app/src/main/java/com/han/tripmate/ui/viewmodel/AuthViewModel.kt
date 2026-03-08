package com.han.tripmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.han.tripmate.data.model.User
import com.han.tripmate.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun login(email: String) {
        // 임시
        _currentUser.value = User(
            id = "user_123",
            email = email,
            nickname = "승환",
            isVerified = true
        )
    }

    // 로그아웃
    fun logout() {
        _currentUser.value = null
    }

    // 모드 토글
    fun toggleRole(isGuide: Boolean) {
        val current = _currentUser.value ?: return
        val newRole = if (isGuide) UserRole.GUIDE else UserRole.USER

        // 기존 유저 정보를 유지하면서 역할(Role)만 교체해서 다시 저장!
        _currentUser.value = current.copy(currentRole = newRole)
    }

}