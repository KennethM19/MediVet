package com.example.medivet.repository

import com.example.medivet.model.User
import com.example.medivet.model.AuthRequest
import com.example.medivet.model.AuthResponse
import com.example.medivet.services.AuthService // Tu interfaz Retrofit
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import com.example.medivet.model.RegisterRequest

class UserRepository(

    private val authService: AuthService,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    // -------------------------------------------------------------------
    //                       NUEVAS FUNCIONES DE LOGIN
    // -------------------------------------------------------------------

    // 1. Login con FastAPI (Llama a tu backend)
    suspend fun loginWithFastApi(email: String, password: String): AuthResponse {
        val request = AuthRequest(email, password)
        // Llama al servicio API y espera la respuesta de Retrofit
        val response: Response<AuthResponse> = authService.loginUser(request)

        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            // Lanza una excepción en caso de error de credenciales o del servidor
            throw Exception("FastAPI: Credenciales incorrectas o error ${response.code()}")
        }
    }

    // 2. Login con Firebase (Adaptado a Corrutinas)
    suspend fun loginWithFirebase(email: String, password: String): Boolean {
        // Convierte la API asíncrona de Firebase a una función suspend
        return suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email.trim(), password.trim())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(true) // Éxito
                    } else {
                        // Pasa el error para que el ViewModel lo maneje
                        continuation.resumeWithException(task.exception ?: Exception("Error desconocido de Firebase"))
                    }
                }
        }
    }

    //NUEVA FUNCIÓN: Registro con FastAPI
    suspend fun registerWithFastApi(request: RegisterRequest): AuthResponse {
        // Llama al servicio API para enviar la solicitud de registro
        val response = authService.registerUser(request)

        if (response.isSuccessful && response.body() != null) {
            // Devuelve el token de autenticación (AuthResponse) si el registro fue exitoso
            return response.body()!!
        } else {
            // Manejo de error (ej. 409 Conflict si el email ya existe)
            // Puedes añadir aquí lógica para leer el cuerpo de error (response.errorBody())
            throw Exception("Registro fallido. El email podría estar en uso, o error: ${response.code()}")
        }
    }

    // -------------------------------------------------------------------
    //                       FUNCIONES EXISTENTES
    // -------------------------------------------------------------------

    // Tu función existente para obtener el usuario de Firebase
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

    // Tu función existente para cerrar sesión
    fun signOut() {
        auth.signOut()
    }
}