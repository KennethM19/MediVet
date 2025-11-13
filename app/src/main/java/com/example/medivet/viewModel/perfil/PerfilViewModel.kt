package com.example.medivet.viewModel.perfil

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medivet.model.model.User
import com.example.medivet.model.repository.ProfileRepository
import com.example.medivet.model.repository.UserRepository
import com.example.medivet.model.services.ApiClient
import com.example.medivet.model.services.FirebaseStorageService
import com.example.medivet.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.asStateFlow
import java.lang.Exception


class PerfilViewModel(
    private val context: Context,
    private val sessionManager: SessionManager
) : ViewModel() {

    // ProfileRepository inicializado con las dependencias correctas
    private val profileRepository = ProfileRepository(
        authService = ApiClient.apiService,
        context = context,
        firebaseStorageService = FirebaseStorageService(context)
    )

    // Estados de la UI
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _uploadSuccess = MutableStateFlow(false)
    val uploadSuccess: StateFlow<Boolean> = _uploadSuccess.asStateFlow()

    init {
        loadUserData()
    }

    /**
     * Carga los datos del usuario autenticado desde el backend.
     * Usa ProfileRepository que obtiene el usuario por email del token.
     */
    fun loadUserData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d("PerfilViewModel", "🔄 Cargando datos del usuario...")

                val userData = profileRepository.getCurrentUserData()

                if (userData != null) {
                    _user.value = userData
                    _errorMessage.value = null
                    Log.d("PerfilViewModel", "✅ Usuario cargado: ${userData.name} ${userData.lastname}")
                    Log.d("PerfilViewModel", "📧 Email: ${userData.email}")
                    Log.d("PerfilViewModel", "📸 Foto: ${userData.photo ?: "Sin foto"}")
                } else {
                    _errorMessage.value = "No se pudo cargar los datos del usuario"
                    Log.e("PerfilViewModel", "❌ Usuario no encontrado")
                }
            } catch (e: Exception) {
                Log.e("PerfilViewModel", "❌ Error al cargar usuario: ${e.message}", e)
                _errorMessage.value = "Error al cargar datos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Sube una foto de perfil del usuario.
     * Actualiza automáticamente el estado con los datos del usuario actualizado.
     */
    fun uploadProfilePhoto(photoUri: Uri) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _uploadSuccess.value = false
                Log.d("PerfilViewModel", "📸 Iniciando carga de foto de perfil...")

                val token = sessionManager.getToken()

                if (token.isNullOrEmpty()) {
                    _errorMessage.value = "Sesión expirada. Por favor, inicia sesión nuevamente"
                    Log.e("PerfilViewModel", "❌ Token es nulo o vacío")
                    _isLoading.value = false
                    return@launch
                }

                Log.d("PerfilViewModel", "🔑 Token obtenido (primeros 20 chars): ${token.take(20)}...")

                // Subir foto y obtener usuario actualizado
                val updatedUser = profileRepository.uploadAndUpdateProfilePhoto(photoUri, token)

                if (updatedUser != null) {
                    _user.value = updatedUser
                    _uploadSuccess.value = true
                    _errorMessage.value = null
                    Log.d("PerfilViewModel", "✅ Foto actualizada exitosamente")
                    Log.d("PerfilViewModel", "👤 Usuario: ${updatedUser.name} ${updatedUser.lastname}")
                    Log.d("PerfilViewModel", "📸 Nueva foto URL: ${updatedUser.photo}")
                } else {
                    _errorMessage.value = "Error al subir la foto. Por favor, intenta de nuevo"
                    Log.e("PerfilViewModel", "❌ El repositorio devolvió un usuario nulo")
                }
            } catch (e: Exception) {
                Log.e("PerfilViewModel", "❌ Excepción al subir foto: ${e.message}", e)
                _errorMessage.value = "Error inesperado: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Refresca los datos del usuario desde el backend.
     * Útil para actualizar la UI después de cambios externos.
     */
    fun refreshUserData() {
        loadUserData()
    }

    /**
     * Limpia el mensaje de error.
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * Limpia el estado de éxito de carga.
     */
    fun clearUploadSuccess() {
        _uploadSuccess.value = false
    }
}