package com.example.medivet.viewModel.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medivet.model.repository.UserRepository
import com.example.medivet.model.services.ApiClient
import com.example.medivet.utils.SessionManager
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val token: String, val method: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class LoginViewModel(
    private val repository: UserRepository = UserRepository(ApiClient.apiService),
    private val sessionManager: SessionManager
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
                val response = repository.loginWithFastApi(email, password)
                val token = response.access_token
                val method = "FastAPI"

                sessionManager.saveAuthData(token, method)

                _authState.value = AuthState.Success(token, method)

            } catch (fastApiError: Exception) {
                Log.e(
                    "LoginVM",
                    "FastAPI falló: ${fastApiError.message}. Intentando con Firebase..."
                )

                try {
                    repository.loginWithFirebase(email, password)
                    val token = "FIREBASE_TOKEN"
                    val method = "Firebase"

                    sessionManager.saveAuthData(token, method)
                    _authState.value = AuthState.Success(token, method)

                } catch (firebaseError: Exception) {
                    val msg = firebaseError.message ?: "Fallo de autenticación en ambos sistemas."
                    _authState.value = AuthState.Error(msg)
                }
            }
        }
    }

    fun signInWithGoogle(credential: AuthCredential) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                repository.loginWithGoogleCredential(credential)

                val token = "FIREBASE_GOOGLE_TOKEN"
                val method = "Google" // Método separado
                sessionManager.saveAuthData(token, method)


                _authState.value = AuthState.Success(token, method)

            } catch (googleError: Exception) {
                _authState.value = AuthState.Error(googleError.message ?: "Error de login con Google")
            }
        }
    }

    fun setGoogleApiError(message: String) {
        _authState.value = AuthState.Error(message)
    }

}
