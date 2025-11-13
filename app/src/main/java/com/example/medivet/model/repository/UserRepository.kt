package com.example.medivet.model.repository

import android.util.Log
import com.example.medivet.model.model.User
import com.example.medivet.model.model.AuthRequest
import com.example.medivet.model.model.AuthResponse
import com.example.medivet.model.model.RegisterRequest
import com.example.medivet.model.services.AuthService
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UserRepository(

    private val authService: AuthService,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    suspend fun registerWithFastApi(request: RegisterRequest): AuthResponse {
        val response = authService.registerUser(request)

        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw Exception("Registro fallido. El email podría estar en uso, o error: ${response.code()}")
        }
    }
    suspend fun loginWithFastApi(email: String, password: String): AuthResponse {
        val request = AuthRequest(email, password)
        val response: Response<AuthResponse> = authService.loginUser(request)

        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw Exception("FastAPI: Credenciales incorrectas o error ${response.code()}")
        }
    }

    suspend fun loginWithFirebase(email: String, password: String): Boolean {
        return suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email.trim(), password.trim())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(true)
                    } else {

                        continuation.resumeWithException(
                            task.exception ?: Exception("Error desconocido de Firebase")
                        )
                    }
                }
        }
    }

    suspend fun loginWithGoogleCredential(credential: AuthCredential): Boolean {
        return suspendCoroutine { continuation ->
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(true)
                    } else {
                        continuation.resumeWithException(
                            task.exception ?: Exception("Error desconocido de Google Auth")
                        )
                    }
                }
        }
    }

    suspend fun getCurrentUser(): User? {
        return try {
            val response = authService.getCurrentUser()
            if (response.isSuccessful && response.body() != null) {
                response.body()?.firstOrNull()
            } else {
                Log.e("UserRepository", "Error: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Excepción: ${e.message}")
            null
        }
    }

    fun signOut() {
        auth.signOut()
    }
}