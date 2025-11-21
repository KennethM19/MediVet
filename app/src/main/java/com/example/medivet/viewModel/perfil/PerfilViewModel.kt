package com.example.medivet.viewModel.perfil

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medivet.model.model.User
import com.example.medivet.model.repository.ProfileRepository
import com.example.medivet.model.services.ApiClient
import com.example.medivet.model.services.FirebaseStorageService
import com.example.medivet.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.asStateFlow
import java.lang.Exception


class PerfilViewModel(
    private val context: Context,       // Se inyecta el contexto
    private val sessionManager: SessionManager      // Se inyecta SessionManager (Preferences DataStore)
) : ViewModel() {

    // Repositorio de perfil (Capa de Datos + Patrón Repositorio)
    private val profileRepository = ProfileRepository(
        authService = ApiClient.apiService,     // Retrofit (HTTP + REST)
        context = context,
        firebaseStorageService = FirebaseStorageService(context)  // Servicio de almacenamiento remoto
    )

    // StateFlow: manejo de estado reactivo de la UI
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _uploadSuccess = MutableStateFlow(false)
    val uploadSuccess: StateFlow<Boolean> = _uploadSuccess.asStateFlow()

    init {
        loadUserData()  // Carga inicial de datos por REST
    }

    /**
     * Obtiene los datos del usuario desde la API REST.
     * Llama al repositorio.
     * Se ejecuta dentro de una corrutina (no bloquea el hilo principal).
     */
    fun loadUserData() {
        viewModelScope.launch {         // Corrutina del ViewModel
            try {
                _isLoading.value = true

                val userData = profileRepository.getCurrentUserData()     // Retrofit -> REST API

                if (userData != null) {
                    _user.value = userData        // Actualiza la UI
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "No se pudo cargar los datos del usuario"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar datos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Sube una foto al servidor (Firebase Storage) y actualiza el perfil vía API REST.
     * Combina dos fuentes de datos (Storage + Backend) - Patrón Repositorio.
     */
    fun uploadProfilePhoto(photoUri: Uri) {
        viewModelScope.launch {         // Corrutina para tarea larga
            try {
                _isLoading.value = true
                _uploadSuccess.value = false

                val token = sessionManager.getToken()       // Preferences DataStore: recuperación del token

                if (token.isNullOrEmpty()) {
                    _errorMessage.value = "Sesión expirada. Por favor, inicia sesión nuevamente"
                    _isLoading.value = false
                    return@launch
                }

                // Subir foto y obtener usuario actualizado
                val updatedUser = profileRepository.uploadAndUpdateProfilePhoto(photoUri, token)

                if (updatedUser != null) {
                    _user.value = updatedUser       // Actualiza estado de UI
                    _uploadSuccess.value = true
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Error al subir la foto. Por favor, intenta de nuevo"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error inesperado: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

     //Refresca los datos del usuario desde el backend (API REST).
    //Útil para actualizar la UI después de cambios externos.
    fun refreshUserData() {
        loadUserData()
    }

     //Limpiar el mensaje de error.
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    //Limpia el estado de éxito del upload.
    fun clearUploadSuccess() {
        _uploadSuccess.value = false
    }
}