package com.example.medivet.ui.pets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medivet.model.PetRequest
import com.example.medivet.repository.PetRepository
import com.example.medivet.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class DropdownOption(val id: Int, val name: String)

sealed class PetCreationState {
    object Idle : PetCreationState()
    object Loading : PetCreationState()
    object Success : PetCreationState()
    data class Error(val message: String) : PetCreationState()
}

class CreatePetViewModel(
    private val repository: PetRepository = PetRepository(),
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _sexOptions = MutableStateFlow<List<DropdownOption>>(emptyList())
    val sexOptions: StateFlow<List<DropdownOption>> = _sexOptions.asStateFlow()

    private val _speciesOptions = MutableStateFlow<List<DropdownOption>>(emptyList())
    val speciesOptions: StateFlow<List<DropdownOption>> = _speciesOptions.asStateFlow()

    private val _breedOptions = MutableStateFlow<List<DropdownOption>>(emptyList())
    val breedOptions: StateFlow<List<DropdownOption>> = _breedOptions.asStateFlow()

    private val _creationState = MutableStateFlow<PetCreationState>(PetCreationState.Idle)
    val creationState: StateFlow<PetCreationState> = _creationState.asStateFlow()

    // 🔹 Cargar los dropdowns desde el backend
    fun loadDropdownData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val sexResponse = repository.getSexOptions()
                val speciesResponse = repository.getSpeciesOptions()
                val breedResponse = repository.getBreedOptions()

                if (sexResponse.isSuccessful && speciesResponse.isSuccessful && breedResponse.isSuccessful) {
                    _sexOptions.value = sexResponse.body()?.map { map ->
                        val id = (map["id"] as? Double)?.toInt() ?: 0
                        val name = map["name"] as? String ?: ""
                        DropdownOption(id, name)
                    } ?: emptyList()

                    _speciesOptions.value = speciesResponse.body()?.map { map ->
                        val id = (map["id"] as? Double)?.toInt() ?: 0
                        val name = map["name"] as? String ?: ""
                        DropdownOption(id, name)
                    } ?: emptyList()

                    _breedOptions.value = breedResponse.body()?.map { map ->
                        val id = (map["id"] as? Double)?.toInt() ?: 0
                        val name = map["name"] as? String ?: ""
                        DropdownOption(id, name)
                    } ?: emptyList()
                } else {
                    _creationState.value = PetCreationState.Error("Error al cargar opciones del servidor")
                }

            } catch (e: Exception) {
                _creationState.value = PetCreationState.Error("Error cargando opciones: ${e.message}")
            }
        }
    }

    // 🔹 Crear mascota
    fun createPet(pet: PetRequest) {
        viewModelScope.launch {
            _creationState.value = PetCreationState.Loading // Poner el estado de carga primero

            val token = sessionManager.token.first() ?: run {
                _creationState.value =
                    PetCreationState.Error("Token no disponible. Inicia sesión de nuevo.")
                return@launch
            }

            try {
                // Asegúrate de que tu repositorio formatea el token como "Bearer [token]"
                val response = repository.createPet(token, pet)

                if (response.isSuccessful) {
                    _creationState.value = PetCreationState.Success
                } else {
                    // **MEJORA CRÍTICA AQUÍ**
                    // Intentamos leer el cuerpo del error para obtener un mensaje detallado.
                    val errorBody = response.errorBody()?.string()
                    val detailedError = if (errorBody.isNullOrBlank()) {
                        "Error ${response.code()}: ${response.message()}"
                    } else {
                        "Error ${response.code()}: $errorBody"
                    }
                    _creationState.value = PetCreationState.Error(detailedError)
                }
            } catch (e: Exception) {
                // Captura de errores de red (sin conexión, timeout, etc.)
                e.printStackTrace() // Esencial para ver el error completo en Logcat
                _creationState.value = PetCreationState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}
