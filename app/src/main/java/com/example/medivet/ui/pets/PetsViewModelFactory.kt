package com.example.medivet.ui.pets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medivet.repository.PetRepository
import com.example.medivet.utils.SessionManager
class PetsViewModelFactory(
    private val petRepository: PetRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PetsViewModel::class.java)) {
            return PetsViewModel(petRepository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
