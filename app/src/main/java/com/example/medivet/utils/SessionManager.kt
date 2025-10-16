package com.example.medivet.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import android.util.Base64
import kotlinx.coroutines.flow.first
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore("user_session")

class SessionManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("access_token")
        private val METHOD_KEY = stringPreferencesKey("auth_method")
    }

    // Guardar token + método
    suspend fun saveAuthData(token: String, method: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[METHOD_KEY] = method
        }
    }

    suspend fun getUserIdFromToken(): Int? {
        val token = token.first() ?: return null
        return try {
            val parts = token.split(".")
            if (parts.size == 3) {
                val payload = String(Base64.decode(parts[1], Base64.DEFAULT))
                val json = JSONObject(payload)
                json.optInt("user_id", -1).takeIf { it != -1 }
            } else null
        } catch (e: Exception) {
            null
        }
    }

    // Leer token
    val token: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[TOKEN_KEY]
    }

    // Leer método de autenticación (FastAPI o Firebase)
    val method: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[METHOD_KEY]
    }

    // Borrar sesión (logout)
    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
