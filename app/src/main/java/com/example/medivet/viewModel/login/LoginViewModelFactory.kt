package com.example.medivet.viewModel.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medivet.model.repository.UserRepository
import com.example.medivet.model.services.ApiClient
import com.example.medivet.utils.SessionManager

class LoginViewModelFactory(
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            val repository = UserRepository(ApiClient.apiService)
            return LoginViewModel(repository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
