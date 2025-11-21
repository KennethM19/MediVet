package com.example.medivet.viewModel.perfil

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medivet.utils.SessionManager

class PerfilViewModelFactory(
    private val context: Context,       // Dependencia inyectada manualmente (DI)
    private val sessionManager: SessionManager      // Manejo de sesión mediante DataStore/Preferences
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verifica que el ViewModel solicitado sea PerfilViewModel
        if (modelClass.isAssignableFrom(PerfilViewModel::class.java)) {
            // Retorna instancia del ViewModel con sus dependencias inyectadas (DI)
            return PerfilViewModel(context, sessionManager) as T
        }
        // Control de errores si el ViewModel solicitado no coincide
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}