package com.example.medivet.ui.pets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medivet.model.Pet
import com.example.medivet.repository.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
class PetsViewModel : ViewModel() {

    private val repository = PetRepository()

    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
    val pets: StateFlow<List<Pet>> = _pets

    init {
        loadPets()
    }

    private fun loadPets() {
        viewModelScope.launch {
            _pets.value = repository.getUserPets()
        }
    }
}
