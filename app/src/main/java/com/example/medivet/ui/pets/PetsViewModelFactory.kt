package com.example.medivet.ui.pets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medivet.repository.PetRepository
import com.example.medivet.utils.SessionManager

/**
 * Fábrica (Factory) para crear instancias de PetsViewModel.
 *
 * Esta clase es necesaria porque PetsViewModel tiene un constructor que
 * requiere dependencias (PetRepository y SessionManager), y el sistema
 * necesita saber cómo proporcionar esas dependencias al crear el ViewModel.
 */
class PetsViewModelFactory(
    private val petRepository: PetRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    /**
     * Este método es llamado por el sistema para crear el ViewModel.
     * Sobreescribe el método 'create' de la interfaz ViewModelProvider.Factory.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Comprueba si la clase del ViewModel que se solicita es PetsViewModel
        if (modelClass.isAssignableFrom(PetsViewModel::class.java)) {
            // Si lo es, devuelve una nueva instancia de PetsViewModel,
            // pasándole las dependencias que esta fábrica recibió en su constructor.
            return PetsViewModel(petRepository, sessionManager) as T
        }
        // Si se intenta usar esta fábrica para crear un ViewModel desconocido,
        // lanza una excepción para señalar el error.
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
