package com.han.tripmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.han.tripmate.data.model.User
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
            nickname = "여행마스터",
            isVerified = true
        )
    }

    // 로그아웃
    fun logout() {
        _currentUser.value = null
    }
}