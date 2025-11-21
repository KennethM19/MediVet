package com.example.medivet.model.services

import android.content.Context
import android.util.Log
import com.example.medivet.utils.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {

    // Se crea SessionManager para poder obtener el token almacenado en DataStore
    // Es importante porque añade el token automáticamente en todas las llamadas al backend.
    private val sessionManager by lazy { SessionManager(context) }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Obtener el token desde DataStore.
        // Como los interceptores no permiten llamadas suspend, se usa runBlocking.
        val token = runBlocking {
            sessionManager.getToken()
        }

        // Si no hay token, se envía la petición sin el header Authorization.
        // Esto es útil en pantallas como login y registro.
        if (token.isNullOrEmpty()) {
            Log.d("AuthInterceptor", "No hay token disponible, procediendo sin Authorization")
            return chain.proceed(originalRequest)
        }

        // Si existe token, Agregar el header Authorization
        // Esto permite que las peticiones protegidas por JWT funcionen,
        // incluyendo la que obtiene los datos del usuario en PerfilViewModel
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        // Log útil para depuración
        Log.d("AuthInterceptor", "Token agregado a la solicitud: ${token.take(20)}...")
        Log.d("AuthInterceptor", "URL: ${newRequest.url}")

        return chain.proceed(newRequest)
    }
}