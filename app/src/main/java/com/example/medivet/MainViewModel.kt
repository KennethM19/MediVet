package com.example.medivet

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medivet.model.model.User
import com.example.medivet.model.repository.UserRepository
import com.example.medivet.model.services.ApiClient
import com.example.medivet.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MainViewModel(
    private val userRepository: UserRepository = UserRepository(ApiClient.apiService),
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            Log.d("MainViewModel", "Iniciando carga de usuario...")
            try {
                val endpoint = "https://medivet-backend.onrender.com/users/"
                val response = sessionManager.fetchUserData(endpoint)

                if (response != null) {
                    Log.d("MainViewModel", "Respuesta recibida")

                    // Deserializar como array de usuarios
                    val usersList: List<User> = Json.decodeFromString(response)
                    Log.d("MainViewModel", "Array de usuarios desserializado: ${usersList.size} usuarios")

                    // Obtener el email del usuario logueado desde el token
                    val emailLogueado = sessionManager.getEmailFromToken()
                    Log.d("MainViewModel", "Email logueado: $emailLogueado")

                    // Buscar al usuario logueado en la lista
                    val usuarioActual = usersList.firstOrNull { it.email == emailLogueado }

                    if (usuarioActual != null) {
                        _user.value = usuarioActual
                        Log.d("MainViewModel", "Usuario cargado: ${_user.value}")
                    } else {
                        Log.e("MainViewModel", "Usuario no encontrado en la lista. Email buscado: $emailLogueado")
                        if (usersList.isNotEmpty()) {
                            _user.value = usersList[0]
                            Log.d("MainViewModel", "Usando primer usuario como fallback: ${_user.value}")
                        }
                    }
                } else {
                    Log.e("MainViewModel", "Respuesta nula del servidor")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error al cargar usuario: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            sessionManager.clearSession()
            userRepository.signOut()
        }
    }
}