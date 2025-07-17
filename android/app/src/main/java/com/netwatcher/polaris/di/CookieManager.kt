package com.netwatcher.polaris.di

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "polaris_auth_preferences")

object CookieManager {

    private lateinit var appContext: Context
    private val tokenKey = stringPreferencesKey("polaris_token")
    private val emailKey = stringPreferencesKey("polaris_email")

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    suspend fun saveToken(token: String) {
        appContext.dataStore.edit { prefs ->
            prefs[tokenKey] = token
        }
    }

    suspend fun saveEmail(email: String) { // 👈 Added
        appContext.dataStore.edit { prefs ->
            prefs[emailKey] = email
        }
    }

    fun getToken(): Flow<String?> {
        return appContext.dataStore.data.map { prefs ->
            prefs[tokenKey] ?: ""
        }
    }

    fun getEmail(): Flow<String?> { // 👈 Added
        return appContext.dataStore.data.map { prefs ->
            prefs[emailKey] ?: ""
        }
    }

    suspend fun clearToken() {
        appContext.dataStore.edit { prefs ->
            prefs.remove(tokenKey)
        }
    }

    suspend fun clearEmail() { // 👈 Added
        appContext.dataStore.edit { prefs ->
            prefs.remove(emailKey)
        }
    }

    suspend fun clearAll() {
        appContext.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
