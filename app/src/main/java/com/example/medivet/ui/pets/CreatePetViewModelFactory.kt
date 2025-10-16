package com.example.medivet.ui.pets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medivet.utils.SessionManager

class CreatePetViewModelFactory(
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreatePetViewModel::class.java)) {
            return CreatePetViewModel(sessionManager = sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
