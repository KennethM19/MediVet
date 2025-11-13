package com.example.medivet

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medivet.model.model.User
import com.example.medivet.model.repository.ProfileRepository
import com.example.medivet.model.services.ApiClient
import com.example.medivet.model.services.FirebaseStorageService
import com.example.medivet.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val sessionManager: SessionManager,
    context: Context
) : ViewModel() {

    private val profileRepository = ProfileRepository(
        authService = ApiClient.apiService,
        context = context,
        firebaseStorageService = FirebaseStorageService(context)
    )

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadUser()
    }

    /**
     * Carga los datos del usuario desde el ProfileRepository.
     * Usa el endpoint getUserByEmail para obtener datos actualizados.
     */
    private fun loadUser() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d("MainViewModel", "🔄 Cargando usuario desde ProfileRepository...")

                val userData = profileRepository.getCurrentUserData()

                if (userData != null) {
                    _user.value = userData
                    Log.d("MainViewModel", "✅ Usuario cargado en MainScreen")
                    Log.d("MainViewModel", "👤 Nombre: ${userData.name} ${userData.lastname}")
                    Log.d("MainViewModel", "📧 Email: ${userData.email}")
                    Log.d("MainViewModel", "📸 Foto: ${userData.photo ?: "Sin foto"}")
                } else {
                    Log.e("MainViewModel", "❌ No se pudo cargar el usuario")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "❌ Error al cargar usuario: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Refresca los datos del usuario.
     * Llamar cuando se vuelve a MainScreen desde otra pantalla.
     */
    fun refreshUser() {
        Log.d("MainViewModel", "🔄 Refrescando datos del usuario...")
        loadUser()
    }

    /**
     * Cierra la sesión del usuario.
     */
    fun signOut() {
        viewModelScope.launch {
            try {
                sessionManager.clearSession()
                _user.value = null
                Log.d("MainViewModel", "🚪 Sesión cerrada")
            } catch (e: Exception) {
                Log.e("MainViewModel", "❌ Error al cerrar sesión: ${e.message}", e)
            }
        }
    }
}