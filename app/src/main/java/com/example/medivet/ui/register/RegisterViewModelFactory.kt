package com.example.medivet.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medivet.repository.UserRepository
import com.example.medivet.services.ApiClient
import com.example.medivet.utils.SessionManager // 👈 Importa tu SessionManager de utils
import java.lang.IllegalArgumentException

class RegisterViewModelFactory(
    // Recibe la instancia única del SessionManager
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {

            // Crea el Repositorio (que no tiene lógica asíncrona en el constructor)
            val repository = UserRepository(ApiClient.apiService)

            // Crea y retorna el ViewModel, pasándole las dependencias
            return RegisterViewModel(repository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}