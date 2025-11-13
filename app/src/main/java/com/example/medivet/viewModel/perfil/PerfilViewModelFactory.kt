package com.example.medivet.viewModel.perfil

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.medivet.model.repository.ProfileRepository
import com.example.medivet.model.repository.UserRepository
import com.example.medivet.model.services.ApiClient
import com.example.medivet.model.services.FirebaseStorageService
import com.example.medivet.utils.SessionManager

class PerfilViewModelFactory(
    private val context: Context,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PerfilViewModel::class.java)) {
            return PerfilViewModel(context, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}