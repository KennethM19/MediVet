package com.example.medivet.model.repository

import android.content.Context
import android.util.Log
import com.example.medivet.model.local.database.AppDatabase
import com.example.medivet.model.local.entities.PetsByNeuteredEntity
import com.example.medivet.model.local.entities.PetsBySpeciesEntity
import com.example.medivet.model.model.ChartData
import com.example.medivet.model.services.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import com.example.medivet.model.model.PetResponse

/**
 * Repositorio para los dashboards.
 * Implementa patrón Repository con caché offline usando Room.
 *
 * Estrategia:
 * 1. Obtiene todas las mascotas del backend usando el endpoint GET /pets
 * 2. Procesa los datos localmente para crear las estadísticas
 * 3. Guarda en Room para acceso offline
 * 4. Usa Flow para actualizaciones reactivas en la UI
 */
class DashboardRepository(context: Context) {

    private val dao = AppDatabase.getDatabase(context).dashboardDao()
    private val apiService = ApiClient.dashboardService

    companion object {
        private const val TAG = "DashboardRepository"
        private const val CACHE_VALIDITY_MS = 5 * 60 * 1000L // 5 minutos
    }

    /**
     * Obtiene datos de mascotas por especie desde Room.
     * Flow para updates en tiempo real.
     */
    fun getPetsBySpeciesFlow(): Flow<List<ChartData>> {
        return dao.getPetsBySpecies().map { entities ->
            entities.map { entity ->
                ChartData(
                    label = entity.especie,
                    value = entity.cantidad.toFloat()
                )
            }
        }
    }

    /**
     * Obtiene datos de mascotas por castración desde Room.
     * Flow para updates en tiempo real.
     */
    fun getPetsByNeuteredFlow(): Flow<List<ChartData>> {
        return dao.getPetsByNeutered().map { entities ->
            entities.map { entity ->
                ChartData(
                    label = entity.estado,
                    value = entity.cantidad.toFloat()
                )
            }
        }
    }

    /**
     * Sincroniza todos los datos de dashboard con el backend.
     * Obtiene todas las mascotas y procesa las estadísticas localmente.
     */
    suspend fun syncAllData(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "🔄 Sincronizando datos del dashboard...")

                // Obtener todas las mascotas del backend
                val response = apiService.getAllPets(limit = 1000)

                if (!response.isSuccessful) {
                    val error = "Error ${response.code()}: ${response.message()}"
                    Log.e(TAG, "❌ $error")
                    return@withContext Result.failure(Exception(error))
                }

                val pets = response.body() ?: emptyList()
                Log.d(TAG, "✅ ${pets.size} mascotas obtenidas del backend")

                // Procesar estadísticas de especies
                val speciesStats = processSpeciesData(pets)
                dao.replacePetsBySpecies(speciesStats)
                Log.d(TAG, "✅ Estadísticas de especies guardadas")

                // Procesar estadísticas de castración
                val neuteredStats = processNeuteredData(pets)
                dao.replacePetsByNeutered(neuteredStats)
                Log.d(TAG, "✅ Estadísticas de castración guardadas")

                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Excepción al sincronizar: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Procesa los datos de mascotas para crear estadísticas por especie.
     * Agrupa por specie_id y cuenta las mascotas.
     */
    private fun processSpeciesData(pets: List<PetResponse>): List<PetsBySpeciesEntity> {
        // Mapeo de IDs a nombres de especies (ajustar según tu BD)
        val speciesMap = mapOf(
            1 to "Perro",
            2 to "Gato"
        )

        return pets
            .groupBy { it.specie_id }
            .map { (specieId, petsList) ->
                PetsBySpeciesEntity(
                    especie = speciesMap[specieId] ?: "Especie $specieId",
                    cantidad = petsList.size,
                    lastUpdated = System.currentTimeMillis()
                )
            }
            .sortedByDescending { it.cantidad }
    }

    /**
     * Procesa los datos de mascotas para crear estadísticas por castración.
     * Agrupa por estado de castración.
     */
    private fun processNeuteredData(pets: List<PetResponse>): List<PetsByNeuteredEntity> {
        val castrados = pets.count { it.neutered }
        val noCastrados = pets.size - castrados

        return listOf(
            PetsByNeuteredEntity(
                estado = "Castrado",
                cantidad = castrados,
                lastUpdated = System.currentTimeMillis()
            ),
            PetsByNeuteredEntity(
                estado = "No castrado",
                cantidad = noCastrados,
                lastUpdated = System.currentTimeMillis()
            )
        ).filter { it.cantidad > 0 } // Solo incluir si hay mascotas
    }

    /**
     * Verifica si la caché es válida (menos de 5 minutos de antigüedad).
     */
    suspend fun isCacheValid(): Boolean {
        return withContext(Dispatchers.IO) {
            val lastUpdate = dao.getLastUpdateTime() ?: 0L
            val currentTime = System.currentTimeMillis()
            val isValid = (currentTime - lastUpdate) < CACHE_VALIDITY_MS

            Log.d(TAG, "Cache válida: $isValid (última actualización: ${(currentTime - lastUpdate) / 1000}s atrás)")
            isValid
        }
    }
}