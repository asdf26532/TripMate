package com.han.tripmate.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {

        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_NICKNAME = stringPreferencesKey("user_nickname")
    }

    val userEmail: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[USER_EMAIL] }

    suspend fun saveUser(email: String, nickname: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_EMAIL] = email
            preferences[USER_NICKNAME] = nickname
        }
    }

    // 데이터 삭제 (로그아웃 시)
    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}