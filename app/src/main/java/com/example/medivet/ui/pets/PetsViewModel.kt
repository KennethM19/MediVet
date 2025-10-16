package com.example.medivet.ui.pets // 1. Corregido el nombre del paquete para seguir la convención

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medivet.model.PetRequest
import com.example.medivet.model.PetResponse
import com.example.medivet.repository.PetRepository
import com.example.medivet.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// 2. Añade SessionManager como dependencia en el constructor
class PetsViewModel(
    private val repository: PetRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _pets = MutableStateFlow<List<PetResponse>>(emptyList())
    val pets: StateFlow<List<PetResponse>> = _pets

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // 3. Llama a la función para cargar las mascotas cuando se crea el ViewModel
    init {
        loadPets()
    }

    // 4. Implementa la función para cargar la lista de mascotas
    private fun loadPets() {
        viewModelScope.launch {
            try {
                // Obtiene el token de forma segura
                val token = sessionManager.token.first() ?: run {
                    _error.value = "Usuario no autenticado."
                    return@launch
                }

                val response = repository.getPets(token) // Asumiendo que tienes un método `getPets` en tu repositorio

                if (response.isSuccessful) {
                    _pets.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error al cargar mascotas: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de red: ${e.message}"
            }
        }
    }

    // Tu función `createPet` (ya estaba bien, solo la mantengo por completitud)
    fun createPet(token: String, pet: PetRequest) {
        viewModelScope.launch {
            try {
                val response = repository.createPet(token, pet)
                if (response.isSuccessful) {
                    // Recarga la lista para mostrar el nuevo elemento
                    loadPets()
                } else {
                    _error.value = "Error al crear mascota: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de red: ${e.message}"
            }
        }
    }
}


