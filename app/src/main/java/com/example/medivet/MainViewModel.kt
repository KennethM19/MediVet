package com.example.medivet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medivet.model.model.User
import com.example.medivet.model.repository.UserRepository
import com.example.medivet.model.services.ApiClient
import com.example.medivet.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository = UserRepository(ApiClient.apiService),
    sessionManager: SessionManager
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            _user.value = userRepository.getCurrentUser()
        }
    }

    fun signOut() {
        userRepository.signOut()
    }
}