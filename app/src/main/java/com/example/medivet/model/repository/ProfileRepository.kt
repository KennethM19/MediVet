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
 * Implementa el patrón Repository de Clean Architecture/MVVM.
 */
class ProfileRepository(
    private val authService: AuthService,
    private val context: Context,
    private val firebaseStorageService: FirebaseStorageService? = null
) {

    private val sessionManager = SessionManager(context)

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
            Log.d("ProfileRepository", "🚀 Iniciando carga de foto de perfil...")

            // Paso 1: Subir foto al storage
            val storageService = firebaseStorageService ?: FirebaseStorageService(context)
            val photoUrl = storageService.uploadUserProfilePhoto(uri, token)

            if (photoUrl.isNullOrEmpty()) {
                Log.e("ProfileRepository", "❌ La URL de la foto es nula después de la carga")
                return null
            }

            Log.d("ProfileRepository", "✅ Foto subida exitosamente: $photoUrl")

            // Paso 2: Obtener los datos actualizados del usuario autenticado
            Log.d("ProfileRepository", "🔄 Obteniendo datos actualizados del usuario...")
            return fetchAuthenticatedUser()  // ✅ AGREGAR 'return' AQUÍ

        } catch (e: Exception) {
            Log.e("ProfileRepository", "❌ Error al subir foto: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }

    /**
     * Obtiene los datos del usuario autenticado usando su email del token JWT.
     * Usa el endpoint específico getUserByEmail (GET /users/email?email=xxx).
     * El AuthInterceptor agrega automáticamente el token de autenticación.
     *
     * @return Usuario autenticado con sus datos actualizados, o null si hay error
     */
    suspend fun fetchAuthenticatedUser(): User? {
        return try {
            // Extraer el email del token JWT
            val userEmail = sessionManager.getEmailFromToken()

            if (userEmail.isNullOrEmpty()) {
                Log.e("ProfileRepository", "❌ No se pudo extraer el email del token")
                return null
            }

            Log.d("ProfileRepository", "📧 Email del usuario autenticado: $userEmail")

            // Obtener el usuario por email
            // El interceptor agrega automáticamente: Authorization: Bearer <token>
            val response = authService.getUserByEmail(userEmail)

            Log.d("ProfileRepository", "📡 Respuesta getUserByEmail - Código: ${response.code()}")

            if (response.isSuccessful) {
                val user = response.body()

                if (user != null) {
                    Log.d("ProfileRepository", "✅ Usuario autenticado obtenido exitosamente")
                    Log.d("ProfileRepository", "👤 ID: ${user.id}")
                    Log.d("ProfileRepository", "👤 Nombre: ${user.name} ${user.lastname}")
                    Log.d("ProfileRepository", "📧 Email: ${user.email}")
                    Log.d("ProfileRepository", "📸 Foto URL: ${user.photo ?: "Sin foto"}")
                    user
                } else {
                    Log.e("ProfileRepository", "❌ Respuesta exitosa pero usuario es nulo")
                    null
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("ProfileRepository", "❌ Error al obtener usuario:")
                Log.e("ProfileRepository", "Código: ${response.code()}")
                Log.e("ProfileRepository", "Mensaje: ${response.message()}")
                Log.e("ProfileRepository", "Error body: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e("ProfileRepository", "❌ Excepción al obtener usuario: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }

    /**
     * Obtiene los datos actuales del usuario autenticado.
     * Útil para refrescar los datos del perfil desde el ViewModel.
     *
     * @return Usuario con datos actuales, o null si hay error
     */
    suspend fun getCurrentUserData(): User? {
        return fetchAuthenticatedUser()
    }
}