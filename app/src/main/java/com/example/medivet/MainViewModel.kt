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
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _isUserLoggedIn = MutableStateFlow<Boolean?>(null)
    val isUserLoggedIn: StateFlow<Boolean?> = _isUserLoggedIn.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    init {
        checkSession()
        loadUser()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val token = sessionManager.token.first()

            _isUserLoggedIn.value = !token.isNullOrBlank()
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            _user.value = userRepository.getCurrentUser()
        }
    }

    fun signOut() {
        viewModelScope.launch {
            userRepository.signOut()
            sessionManager.clearSession()
            _user.value = null
        }
    }
}