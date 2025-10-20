package com.example.medivet.repository

import com.example.medivet.model.AuthRequest
import com.example.medivet.model.AuthResponse
import com.example.medivet.model.User
import com.example.medivet.services.AuthService
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UserRepository(

    private val authService: AuthService,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

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

    fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser
        return firebaseUser?.let {
            User(
                name = it.displayName ?: "[Nombre]",
                email = it.email ?: "example@example.com",
                profileImageUrl = it.photoUrl?.toString()
            )
        }
    }

    fun signOut() {
        auth.signOut()
    }
}