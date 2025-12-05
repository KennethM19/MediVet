package com.example.medivet.utils

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

// DataStore donde se guardan el token y método de autenticación
private val Context.dataStore by preferencesDataStore("user_session")

class SessionManager(private val context: Context) {

    companion object {
        // Clave para guardar el token JWT
        private val TOKEN_KEY = stringPreferencesKey("access_token")
        private val METHOD_KEY = stringPreferencesKey("auth_method")
    }

    suspend fun saveAuthData(token: String, method: String) {
        // Guarda el token en DataStore después del login
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[METHOD_KEY] = method
        }
    }

    suspend fun getToken(): String? {
        // Devuelve el token para enviarlo al backend o al repositorio
        return context.dataStore.data
            .map { prefs -> prefs[TOKEN_KEY] }
            .first()
    }

    suspend fun getEmailFromToken(): String? {
        // Extrae el email del payload del token (se usa en ProfileRepository)
        return try {
            val token = getToken() ?: return null
            val parts = token.split(".")
            if (parts.size != 3) return null

            val payload = String(Base64.decode(parts[1], Base64.DEFAULT))
            val jsonObject = JSONObject(payload)
            val email = jsonObject.optString("sub", null)   // "sub" = email del usuario

            return email
        } catch (e: Exception) {
            Log.e("SessionManager", "Error al extraer email: ${e.message}")
            null
        }
    }

    val token: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[TOKEN_KEY]
    }

    val method: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[METHOD_KEY]
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }

}