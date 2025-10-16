package com.example.medivet.ui.pets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medivet.repository.PetsRepository
import com.example.medivet.model.Pet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddPetViewModel : ViewModel() {

    private val repository = PetsRepository()

    private val _isPetAdded = MutableStateFlow(false)
    val isPetAdded: StateFlow<Boolean> = _isPetAdded

    fun addPet(pet: Pet) {
        viewModelScope.launch {
            val success = repository.addPet(pet)
            _isPetAdded.value = success
        }
    }
}