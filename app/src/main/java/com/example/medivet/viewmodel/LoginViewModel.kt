package com.example.medivet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medivet.repository.UserRepository
import com.example.medivet.services.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

// Estados posibles de la UI
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val token: String, val method: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class LoginViewModel(
    private val repository: UserRepository = UserRepository(ApiClient.apiService)
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email y contraseña no pueden estar vacíos.")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                // PRIMER INTENTO: Login con FastAPI
                val response = repository.loginWithFastApi(email, password)
                _authState.value = AuthState.Success(response.access_token, "FastAPI")

            } catch (fastApiError: Exception) {
                Log.e("LoginVM", "FastAPI falló: ${fastApiError.message}. Intentando con Firebase...")

                try {
                    // SEGUNDO INTENTO: Login con Firebase si FastAPI falla
                    repository.loginWithFirebase(email, password)
                    _authState.value = AuthState.Success("FIREBASE_TOKEN", "Firebase")

                } catch (firebaseError: Exception) {
                    // Fallo TOTAL
                    val msg = firebaseError.message ?: "Fallo de autenticación en ambos sistemas."
                    _authState.value = AuthState.Error(msg)
                }
            }
        }
    }
}