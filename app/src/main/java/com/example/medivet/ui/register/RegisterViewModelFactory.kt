package com.example.medivet.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medivet.repository.UserRepository
import com.example.medivet.services.ApiClient
import com.example.medivet.utils.SessionManager // 👈 Importa tu SessionManager de utils
import java.lang.IllegalArgumentException

class RegisterViewModelFactory(

    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            val repository = UserRepository(ApiClient.apiService)

            return RegisterViewModel(repository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}