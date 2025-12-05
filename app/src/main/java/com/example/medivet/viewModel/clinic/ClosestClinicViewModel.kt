package com.example.medivet.viewModel.clinic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medivet.model.model.ClinicResponse
import com.example.medivet.utils.ClosestClinicUseCase
import com.example.medivet.utils.LocationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClosestClinicViewModel(
    private val useCase: ClosestClinicUseCase,
    private val locationManager: LocationManager,
    private val token: String?
) : ViewModel() {

    private val _closestClinic = MutableStateFlow<ClinicResponse?>(null)
    val closestClinic: StateFlow<ClinicResponse?> = _closestClinic

    fun loadClosestClinic() {
        viewModelScope.launch {

            Log.d("CLINIC_DEBUG", "Iniciando loadClosestClinic")

            if (token == null) {
                Log.d("CLINIC_DEBUG", "ERROR: Token es null")
                return@launch
            }

            if (!locationManager.hasPermission()) {
                Log.d("CLINIC_DEBUG", "ERROR: No hay permisos de ubicación")
                return@launch
            }

            val location = locationManager.getCurrentLocation()
            Log.d("CLINIC_DEBUG", "Ubicación obtenida: $location")

            if (location == null) {
                Log.d("CLINIC_DEBUG", "ERROR: No se pudo obtener la ubicación")
                return@launch
            }

            val clinic = useCase.execute(
                token,
                location.first,
                location.second
            )

            Log.d("CLINIC_DEBUG", "Clínica más cercana: $clinic")

            _closestClinic.value = clinic
        }
    }
}