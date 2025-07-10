package com.netwatcher.polaris.di

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_preferences")

object TokenManager {

    private lateinit var appContext: Context
    private val tokenKey = stringPreferencesKey("key_token")

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    suspend fun saveToken(token: String) {
        appContext.dataStore.edit { prefs ->
            prefs[tokenKey] = token
        }
    }

    fun getToken(): Flow<String?> {
        return appContext.dataStore.data.map { prefs ->
            prefs[tokenKey] ?: ""
        }
    }

    suspend fun clearToken() {
        appContext.dataStore.edit { prefs ->
            prefs.remove(tokenKey)
        }
    }
}
