package com.example.medivet.viewModel.clinic

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medivet.model.repository.ClinicRepository
import com.example.medivet.utils.ClosestClinicUseCase
import com.example.medivet.utils.LocationManager

class ClosestClinicViewModelFactory(
    private val context: Context,
    private val token: String?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val repository = ClinicRepository()
        val useCase = ClosestClinicUseCase(repository)
        val locationManager = LocationManager(context)

        return ClosestClinicViewModel(
            useCase,
            locationManager,
            token
        ) as T
    }
}