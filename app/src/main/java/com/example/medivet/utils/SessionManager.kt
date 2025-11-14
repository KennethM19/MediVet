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

private val Context.dataStore by preferencesDataStore("user_session")

class SessionManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("access_token")
        private val METHOD_KEY = stringPreferencesKey("auth_method")
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun saveAuthData(token: String, method: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[METHOD_KEY] = method
        }
    }

    suspend fun getToken(): String? {
        return context.dataStore.data
            .map { prefs -> prefs[TOKEN_KEY] }
            .first()
    }

    suspend fun getEmailFromToken(): String? {
        return try {
            val token = getToken() ?: return null
            val parts = token.split(".")
            if (parts.size != 3) return null

            val payload = String(Base64.decode(parts[1], Base64.DEFAULT))
            val jsonObject = JSONObject(payload)
            val email = jsonObject.optString("sub", null)

            Log.d("SessionManager", "Email extraído del token: $email")
            return email
        } catch (e: Exception) {
            Log.e("SessionManager", "Error al extraer email: ${e.message}")
            null
        }
    }

    suspend fun fetchUserData(endpoint: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val token = getToken()
                if (token == null) {
                    Log.e("SessionManager", "Token no disponible")
                    return@withContext null
                }

                Log.d("SessionManager", "URL: $endpoint")
                Log.d("SessionManager", "Token: $token")

                val request = Request.Builder()
                    .url(endpoint)
                    .addHeader("Authorization", "Bearer $token")
                    .addHeader("Content-Type", "application/json")
                    .get()
                    .build()

                Log.d("SessionManager", "Request: ${request.url}")

                val response = client.newCall(request).execute()

                Log.d("SessionManager", "Response Code: ${response.code}")
                Log.d("SessionManager", "Response Message: ${response.message}")

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d("SessionManager", "Respuesta exitosa: $responseBody")
                    responseBody
                } else {
                    val errorBody = response.body?.string()
                    Log.e("SessionManager", "Error del servidor - Código: ${response.code}, Mensaje: ${response.message}, Cuerpo: $errorBody")
                    null
                }
            } catch (e: Exception) {
                Log.e("SessionManager", "Excepción en fetchUserData: ${e.javaClass.simpleName} - ${e.message}")
                e.printStackTrace()
                null
            }
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

    suspend fun getUserIdFromToken(): Int? {
        return try {
            val token = token.first() ?: run {
                Log.e("SessionManager", "Token es nulo, no se puede extraer user_id")
                return null
            }

            val parts = token.split(".")
            if (parts.size != 3) {
                Log.e("SessionManager", "Token JWT inválido, no tiene 3 partes")
                return null
            }

            val payload = String(Base64.decode(parts[1], Base64.DEFAULT))
            val jsonObject = JSONObject(payload)

            if (jsonObject.has("user_id")) {
                val userId = jsonObject.optInt("user_id", -1)
                if (userId == -1) {
                    Log.e("SessionManager", "user_id está presente pero no es un Int válido")
                    null
                } else {
                    Log.d("SessionManager", "ID de usuario extraído: $userId")
                    userId
                }
            } else {
                Log.e("SessionManager", "El token no contiene 'user_id'")
                null
            }
        } catch (e: Exception) {
            Log.e("SessionManager", "Error al decodificar token: ${e.message}")
            null
        }
    }

}