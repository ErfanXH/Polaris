package com.netwatcher.polaris.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "polaris_auth_preferences")

@Singleton
class CookieManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val tokenKey = stringPreferencesKey("polaris_token")
    private val emailKey = stringPreferencesKey("polaris_email")

    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs -> prefs[tokenKey] = token }
    }

    suspend fun saveEmail(email: String) {
        context.dataStore.edit { prefs -> prefs[emailKey] = email }
    }

    fun getToken(): Flow<String?> {
        return context.dataStore.data.map { prefs -> prefs[tokenKey] ?: "" }
    }

    fun getEmail(): Flow<String?> {
        return context.dataStore.data.map { prefs -> prefs[emailKey] ?: "" }
    }

    suspend fun clearToken() {
        context.dataStore.edit { prefs -> prefs.remove(tokenKey) }
    }

    suspend fun clearEmail() {
        context.dataStore.edit { prefs -> prefs.remove(emailKey) }
    }

    suspend fun clearAll() {
        context.dataStore.edit { prefs -> prefs.clear() }
    }
}