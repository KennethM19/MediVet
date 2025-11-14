package com.example.medivet.viewModel.pet

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medivet.model.repository.PetRepository
// --- 👇 IMPORTS AÑADIDOS 👇 ---
import com.example.medivet.model.repository.UserRepository
import com.example.medivet.model.services.ApiClient
// --- 👆 IMPORTS AÑADIDOS 👆 ---
import com.example.medivet.utils.SessionManager

class PetsViewModelFactory(
    private val context: Context,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PetsViewModel::class.java)) {

            val petRepository = PetRepository()
            val userRepository = UserRepository(ApiClient.apiService)

            return PetsViewModel(
                repository = petRepository,
                userRepository = userRepository,
                sessionManager = sessionManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}