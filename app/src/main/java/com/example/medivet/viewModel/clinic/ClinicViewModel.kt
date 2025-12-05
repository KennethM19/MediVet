package com.example.medivet.viewModel.clinic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.medivet.model.model.ClinicResponse
import com.example.medivet.model.repository.ClinicRepository
import com.example.medivet.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ClinicViewModel(
    private val repository: ClinicRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _clinics = MutableStateFlow<List<ClinicResponse>>(emptyList())
    val clinics: StateFlow<List<ClinicResponse>> = _clinics.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadClinics()
    }

    fun loadClinics() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = sessionManager.token.first()
                if (token == null) {
                    _error.value = "No autenticado"
                    return@launch
                }

                val response = repository.getClinics(token)
                if (response.isSuccessful && response.body() != null) {
                    _clinics.value = response.body()!!
                } else {
                    _error.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

