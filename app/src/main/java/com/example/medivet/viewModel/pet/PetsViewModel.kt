package com.example.medivet.viewModel.pet

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medivet.model.model.PetRequest
import com.example.medivet.model.model.PetResponse
import com.example.medivet.model.model.PetUpdate
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

    private val _pet = MutableStateFlow<PetResponse?>(null)
    val pet: StateFlow<PetResponse?> = _pet

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadPets()
    }

    fun loadPets() {
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

                val response = repository.getPets(currentUserId)

                if (response.isSuccessful) {
                    _pets.value = response.body() ?: emptyList()
                    Log.d(
                        "PetsViewModel",
                        "Mascotas recibidas (filtradas por API): ${_pets.value.size}"
                    )
                } else {
                    _error.value = "Error al cargar mascotas: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de red: ${e.message}"
                Log.e("PetsViewModel", "Excepción en loadPets", e)
            }
        }
    }

    fun loadPet(petId: Int) {
        viewModelScope.launch {
            try {
                val token = sessionManager.getToken() ?: return@launch
                Log.d("PetsViewModel", "Cargando mascota con id=$petId y token=$token")

                val response = repository.getPet(petId, token)
                Log.d("PetsViewModel", "Respuesta del backend: $response")
                _pet.value = response
            } catch (e: Exception) {
                println("Error cargando mascota: ${e.message}")
            }
        }
    }

    fun deletePet(petId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val token = sessionManager.getToken() ?: return@launch
                val success = repository.deletePet(petId, token)
                if (success) {
                    _pets.value = _pets.value.filter { it.id != petId }
                }
                onResult(success)
            } catch (e: Exception) {
                Log.e("PetsViewModel", "Error eliminando mascota", e)
                onResult(false)
            }
        }
    }

    fun updatePet(
        petId: Int,
        weight: String,
        neutered: Boolean,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val token = sessionManager.getToken() ?: return@launch
                val update = PetUpdate(
                    weight = weight.toDoubleOrNull(),
                    neutered = neutered
                )
                val response = repository.updatePet(petId, update, token)
                _pet.value = response
                onResult(true)
            } catch (e: Exception) {
                Log.e("PetsViewModel", "Error actualizando mascota", e)
                onResult(false)
            }
        }
    }

    fun updatePetPhoto(
        uri: Uri,
        petId: Int,
        context: Context,
        onResult: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val token = sessionManager.getToken() ?: return@launch
                val response = repository.updatePetPhoto(petId, uri, token, context)
                onResult(response.url)
            } catch (e: Exception) {
                Log.e("PetsViewModel", "Error actualizando foto", e)
                onResult(null)
            }
        }
    }


}