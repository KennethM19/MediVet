package com.example.medivet.model.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.medivet.model.model.User
import com.example.medivet.model.services.AuthService
import com.example.medivet.model.services.FirebaseStorageService
import com.example.medivet.utils.SessionManager

/**
 * Repositorio para manejar las operaciones relacionadas con el perfil del usuario.
 */
class ProfileRepository(
    private val authService: AuthService,   // Servicio que hace las llamadas al backend
    private val context: Context,           // Contexto para acceder a recursos locales
    private val firebaseStorageService: FirebaseStorageService? = null  // Servicio para Firebase Storage (inyectable)
) {

    private val sessionManager = SessionManager(context) // Maneja token y datos de sesión del usuario

    /**
     * Sube la foto de perfil del usuario autenticado.
     *
     * Flujo:
     * 1. Sube la foto al Firebase Storage
     * 2. Actualiza la URL de la foto en el backend
     * 3. Obtiene los datos actualizados del usuario por email
     *
     * @param uri URI de la imagen seleccionada desde la galería
     * @param token Token JWT del usuario autenticado
     * @return Usuario actualizado con la nueva foto, o null si hay error
     */
    suspend fun uploadAndUpdateProfilePhoto(uri: Uri, token: String): User? {
        return try {
            // Paso 1: Crear y usar el servicio de Firebase
            val storageService = firebaseStorageService ?: FirebaseStorageService(context)
            // Subir la foto y obtener la URL pública
            val photoUrl = storageService.uploadUserProfilePhoto(uri, token)

            if (photoUrl.isNullOrEmpty()) {
                // Si no se obtuvo URL, significa que falló la subida a Firebase
                return null
            }

            // Paso 2: Obtener actualizados los datos del usuario desde backend
            return fetchAuthenticatedUser()     // Se retorna el usuario actualizado

        } catch (e: Exception) {
            Log.e("ProfileRepository", " Error al subir foto: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }

    /**
     * Obtiene los datos del usuario autenticado usando su email del token JWT.
     * Usa el endpoint getUserByEmail (GET /users/email?email=xxx).
     * El AuthInterceptor agrega automáticamente el token de autenticación.
     */
    suspend fun fetchAuthenticatedUser(): User? {
        return try {
            // Obtener email guardado en el token JWT desde SessionManager
            val userEmail = sessionManager.getEmailFromToken()

            if (userEmail.isNullOrEmpty()) {
                // Si no hay email en el token, no se puede consultar el backend
                return null
            }

            // Obtener el usuario por email
            // El interceptor agrega automáticamente: Authorization: Bearer <token>
            val response = authService.getUserByEmail(userEmail)

            Log.d("ProfileRepository", "📡 Respuesta getUserByEmail - Código: ${response.code()}")

            if (response.isSuccessful) {
                // Si la respuesta HTTP es 200
                val user = response.body()

                if (user != null) {
                    // Usuario recibido correctamente
                    user
                } else {
                    // En casos con respuesta OK pero sin cuerpo
                    Log.e("ProfileRepository", " Respuesta exitosa pero usuario es nulo")
                    null
                }
            } else {
                // Manejo del error si la respuesta no es exitosa
                val errorBody = response.errorBody()?.string()
                null
            }
        } catch (e: Exception) {
            // Manejo de excepciones
            Log.e("ProfileRepository", "Excepción al obtener usuario: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }

    /**
     * Obtiene los datos actuales del usuario autenticado.
     * Útil para refrescar los datos del perfil desde el ViewModel.
     */
    suspend fun getCurrentUserData(): User? {
        // Llama a la función principal de obtención
        return fetchAuthenticatedUser()
    }
}