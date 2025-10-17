package com.example.medivet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medivet.repository.UserRepository
import com.example.medivet.model.User
import com.example.medivet.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first // Para esperar el valor del Flow
import kotlinx.coroutines.launch

class MainViewModel(
    // 👈 1. RECIBE el Repositorio (inyectado por la Factory)
    private val userRepository: UserRepository,
    // 👈 2. RECIBE el SessionManager (inyectado por la Factory)
    private val sessionManager: SessionManager
) : ViewModel() {

    // NUEVO: Estado para comunicar a la UI si el usuario está logueado
    // null: Comprobando la sesión (Loading en Splash Screen)
    // true/false: Sesión definida
    private val _isUserLoggedIn = MutableStateFlow<Boolean?>(null)
    val isUserLoggedIn: StateFlow<Boolean?> = _isUserLoggedIn.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow() // Hacemos el user StateFlow

    init {
        // Ejecutamos la verificación de sesión primero.
        checkSession()
        loadUser()
    }

    // 👈 3. NUEVA FUNCIÓN: Verifica si hay un token guardado en DataStore
    private fun checkSession() {
        viewModelScope.launch {
            // Esperamos el primer valor del Flow de DataStore (el token guardado)
            val token = sessionManager.token.first()
            // Si el token no es nulo o vacío, el usuario está logueado
            _isUserLoggedIn.value = !token.isNullOrBlank()
        }
    }

    private fun loadUser() {
        // Tu código original: Carga los datos del usuario logueado.
        viewModelScope.launch {
            _user.value = userRepository.getCurrentUser()
        }
    }

    // 👈 4. MODIFICACIÓN: Limpiar DataStore al cerrar sesión
    fun signOut() {
        viewModelScope.launch {
            userRepository.signOut() // Cierra sesión en Firebase (si se usó)
            sessionManager.clearSession() // Borra el token de FastAPI de DataStore
            _user.value = null
        }
    }
}