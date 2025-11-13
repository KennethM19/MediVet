package com.example.medivet.model.services

import android.content.Context
import android.util.Log
import com.example.medivet.utils.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {

    private val sessionManager by lazy { SessionManager(context) }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Obtener el token desde DataStore (usando runBlocking ya que estamos en un interceptor)
        val token = runBlocking {
            sessionManager.getToken()
        }

        // Si no hay token, continuar sin modificar
        if (token.isNullOrEmpty()) {
            Log.d("AuthInterceptor", "No hay token disponible, procediendo sin Authorization")
            return chain.proceed(originalRequest)
        }

        // Agregar el header Authorization
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        Log.d("AuthInterceptor", "Token agregado a la solicitud: ${token.take(20)}...")
        Log.d("AuthInterceptor", "URL: ${newRequest.url}")

        return chain.proceed(newRequest)
    }
}