package com.example.medivet.viewModel.dashboard

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medivet.model.model.ChartData
import com.example.medivet.model.repository.DashboardRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.medivet.model.model.VaccineChartData


sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(
        val speciesData: List<ChartData>,
        val neuteredData: List<ChartData>,
        val dogVaccineData: List<VaccineChartData>,
        val catVaccineData: List<VaccineChartData>,
        val isRefreshing: Boolean = false
    ) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}

class DashboardViewModel(context: Context) : ViewModel() {

    private val repository = DashboardRepository(context)

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    companion object {
        private const val TAG = "DashboardViewModel"
    }


    init {
        loadDashboardData()
    }

    /**
     * Carga datos del dashboard.
     * Estrategia:
     * 1. Carga datos de Room (cache) inmediatamente
     * 2. Sincroniza con el backend en segundo plano
     */
    private fun loadDashboardData() {
        viewModelScope.launch {
            try {
                // Observar datos de Room en tiempo real
                combine(
                    repository.getPetsBySpeciesFlow(),
                    repository.getPetsByNeuteredFlow(),
                    repository.getDogVaccineRankingFlow(),
                    repository.getCatVaccineRankingFlow()
                ) { speciesData, neuteredData, dogVaccines, catVaccines ->
                    DashboardUiState.Success(
                        speciesData = speciesData,
                        neuteredData = neuteredData,
                        dogVaccineData = dogVaccines,
                        catVaccineData = catVaccines,
                        isRefreshing = _isRefreshing.value
                    )
                }.collect { state ->
                    _uiState.value = state
                }

            } catch (e: Exception) {
                Log.e(TAG, " Error al cargar datos: ${e.message}", e)
                _uiState.value = DashboardUiState.Error("Error al cargar datos: ${e.message}")
            }
        }

        // Sincronizar con backend en segundo plano
        syncWithBackend()
    }

    /**
     * Sincroniza datos con el backend.
     */
    private fun syncWithBackend() {
        viewModelScope.launch {
            try {
                val result = repository.syncAllDashboardData()

                if (result.isSuccess) {
                } else {
                    Log.w(TAG, " Error en sincronización: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, " Excepción en sincronización: ${e.message}", e)
            }
        }
    }

    /**
     * Refresca datos manualmente (pull-to-refresh).
     */
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true

            val result = repository.syncAllData()

            if (result.isSuccess) {
            } else {
                Log.e(TAG, " Error al refrescar: ${result.exceptionOrNull()?.message}")
            }

            _isRefreshing.value = false
        }
    }
}