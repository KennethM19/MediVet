package com.example.medivet
// NOTA: Ajusta este paquete si tu MainViewModel está en otro lugar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medivet.repository.UserRepository
import com.example.medivet.services.ApiClient // Necesario para instanciar el Repositorio
import com.example.medivet.utils.SessionManager // Tu SessionManager de DataStore
import java.lang.IllegalArgumentException

/**
 * Factory para crear instancias del MainViewModel, inyectando todas sus dependencias.
 */
class MainViewModelFactory(
    // El Factory necesita la instancia única de SessionManager
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {

            // 1. Instanciar el Repositorio (requiere el servicio API)
            val userRepository = UserRepository(ApiClient.apiService)

            // 2. Crear el ViewModel, inyectando el Repositorio y el SessionManager
            return MainViewModel(userRepository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}