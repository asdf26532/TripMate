package com.han.tripmate.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.han.tripmate.data.UserPreferences
import com.han.tripmate.data.model.User
import com.han.tripmate.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val userPrefs = UserPreferences(application)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    init {
        checkSavedUser()
    }

    private fun checkSavedUser() {
        viewModelScope.launch {
            val savedEmail = userPrefs.userEmail.first()
            if (!savedEmail.isNullOrEmpty()) {

                _currentUser.value = User(
                    id = "user_123",
                    email = savedEmail,
                    nickname = "여행자",
                    isVerified = true
                )
            }
        }
    }

    fun login(email: String) {
        // 임시
        val newUser = User(
            id = "user_123",
            email = email,
            nickname = "망고",
            isVerified = true,
            currentRole = UserRole.USER
        )

        _currentUser.value = newUser

        viewModelScope.launch {
            userPrefs.saveUser(newUser.email, newUser.nickname)
        }
    }

    // 로그아웃
    fun logout() {
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
}