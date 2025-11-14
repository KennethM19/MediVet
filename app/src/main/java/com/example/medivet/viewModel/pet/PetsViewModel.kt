package com.example.medivet.viewModel.pet

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medivet.model.model.PetRequest
import com.example.medivet.model.model.PetResponse
import com.example.medivet.model.repository.PetRepository
import com.example.medivet.model.repository.UserRepository
import com.example.medivet.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PetsViewModel(
    private val repository: PetRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _pets = MutableStateFlow<List<PetResponse>>(emptyList())
    val pets: StateFlow<List<PetResponse>> = _pets.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadPets()
    }

    private fun loadPets() {
        viewModelScope.launch {
            try {

                val email = sessionManager.getEmailFromToken()
                if (email == null) {
                    _error.value = "No se pudo leer el email del token."
                    return@launch
                }

                val userResponse = userRepository.getUserByEmail(email)
                if (!userResponse.isSuccessful || userResponse.body() == null) {
                    _error.value = "No se pudo obtener la información del usuario desde la API."
                    return@launch
                }

                val currentUserId = userResponse.body()!!.id
                Log.d("PetsViewModel", "ID de usuario obtenido: $currentUserId")

                val response = repository.getPets(currentUserId)

                if (response.isSuccessful) {
                    _pets.value = response.body() ?: emptyList()
                    Log.d("PetsViewModel", "Mascotas recibidas (filtradas por API): ${_pets.value.size}")
                } else {
                    _error.value = "Error al cargar mascotas: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de red: ${e.message}"
                Log.e("PetsViewModel", "Excepción en loadPets", e)
            }
        }
    }

    fun createPet(pet: PetRequest) {
        viewModelScope.launch {
            try {
                val token = sessionManager.token.first() ?: run {
                    _error.value = "Usuario no autenticado."
                    return@launch
                }
                val response = repository.createPet(token, pet)
                if (response.isSuccessful) {
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