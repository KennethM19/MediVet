package com.example.medivet.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medivet.repository.UserRepository
import com.example.medivet.services.ApiClient
import com.example.medivet.utils.SessionManager
import com.example.medivet.ui.login.LoginViewModel

/**
 * Factory personalizada para crear instancias del LoginViewModel
 * con las dependencias necesarias (Repository + SessionManager).
 */
class LoginViewModelFactory(
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            // Inyecta dependencias en el ViewModel
            val repository = UserRepository(ApiClient.apiService)
            return LoginViewModel(repository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
