package com.example.medivet.model.repository

import android.content.Context
import android.util.Log
import com.example.medivet.model.local.database.AppDatabase
import com.example.medivet.model.local.entities.PetsByNeuteredEntity
import com.example.medivet.model.local.entities.PetsBySpeciesEntity
import com.example.medivet.model.model.ChartData
import com.example.medivet.model.model.PetResponse
import com.example.medivet.model.services.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import com.example.medivet.model.model.PetResponse
import com.example.medivet.model.local.entities.VaccineRankingEntity
import com.example.medivet.model.model.PetVaccineResponse
import com.example.medivet.model.model.VaccineChartData


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
        const val SPECIES_DOG = 1
        const val SPECIES_CAT = 2
        private const val CACHE_VALIDITY_MS = 2 * 60 * 1000L // 2 minutos
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
                // Obtener todas las mascotas del backend
                val response = apiService.getAllPets(limit = 1000)

                if (!response.isSuccessful) {
                    val error = "Error ${response.code()}: ${response.message()}"
                    Log.e(TAG, " $error")
                    return@withContext Result.failure(Exception(error))
                }

                val pets = response.body() ?: emptyList()
                Log.d(TAG, " ${pets.size} mascotas obtenidas del backend")

                // Procesar estadísticas de especies
                val speciesStats = processSpeciesData(pets)
                dao.replacePetsBySpecies(speciesStats)

                // Procesar estadísticas de castración
                val neuteredStats = processNeuteredData(pets)
                dao.replacePetsByNeutered(neuteredStats)

                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "Excepción al sincronizar: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Procesa los datos de mascotas para crear estadísticas por especie.
     * Agrupa por specie_id y cuenta las mascotas.
     */
    private fun processSpeciesData(pets: List<PetResponse>): List<PetsBySpeciesEntity> {
        val speciesMap = mapOf(
            1 to "Perro",
            2 to "Gato"
        )

        return pets
            .groupBy { it.species?.id }
            .map { (specieId, petsList) ->
                PetsBySpeciesEntity(
                    especie = speciesMap[specieId] ?: petsList.firstOrNull()?.species?.name
                    ?: "Desconocido",
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



    // ========== RANKING DE VACUNAS ==========

    /**
     * Obtiene el ranking de vacunas para perros desde Room.
     * Flow para updates en tiempo real.
     */
    fun getDogVaccineRankingFlow(): Flow<List<VaccineChartData>> {
        return dao.getVaccineRankingBySpecies(SPECIES_DOG).map { entities ->
            entities.map { entity ->
                VaccineChartData(
                    vaccineName = entity.vaccineName,
                    count = entity.count.toFloat()
                )
            }
        }
    }

    /**
     * Obtiene el ranking de vacunas para gatos desde Room.
     * Flow para updates en tiempo real.
     */
    fun getCatVaccineRankingFlow(): Flow<List<VaccineChartData>> {
        return dao.getVaccineRankingBySpecies(SPECIES_CAT).map { entities ->
            entities.map { entity ->
                VaccineChartData(
                    vaccineName = entity.vaccineName,
                    count = entity.count.toFloat()
                )
            }
        }
    }

    /**
     * Sincroniza el ranking de vacunas para ambas especies.
     */
    suspend fun syncVaccineRankings(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {

                // Sincronizar perros
                val dogResult = syncVaccineRankingBySpecies(
                    speciesId = SPECIES_DOG,
                    speciesName = "Perro"
                )

                // Sincronizar gatos
                val catResult = syncVaccineRankingBySpecies(
                    speciesId = SPECIES_CAT,
                    speciesName = "Gato"
                )

                if (dogResult.isSuccess && catResult.isSuccess) {
                    Result.success(Unit)
                } else {
                    val error = "Error al sincronizar rankings"
                    Log.e(TAG, " $error")
                    Result.failure(Exception(error))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción al sincronizar rankings: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Sincroniza el ranking de vacunas para una especie específica.
     *
     * Flujo:
     * 1. Obtiene todas las aplicaciones de vacunas del backend
     * 2. Agrupa por vaccine_type.name y cuenta
     * 3. Ordena por cantidad (descendente)
     * 4. Toma top 10
     * 5. Guarda en Room
     */
    private suspend fun syncVaccineRankingBySpecies(
        speciesId: Int,
        speciesName: String
    ): Result<Unit> {
        return try {
            val response = apiService.getPetVaccines(specieId = speciesId)

            if (!response.isSuccessful) {
                val error = "Error ${response.code()}: ${response.message()}"
                Log.e(TAG, " $error")
                return Result.failure(Exception(error))
            }

            val vaccineApplications = response.body() ?: emptyList()

            if (vaccineApplications.isEmpty()) {
                // Limpiar cache si no hay datos
                dao.clearVaccineRankingBySpecies(speciesId)
                return Result.success(Unit)
            }

            // Procesar datos: agrupar y contar
            val ranking = processVaccineRanking(vaccineApplications, speciesId, speciesName)

            // Guardar en Room
            dao.replaceVaccineRankingBySpecies(speciesId, ranking)

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al sincronizar $speciesName: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Procesa las aplicaciones de vacunas para crear el ranking.
     *
     * @param applications Lista de aplicaciones de vacunas
     * @param speciesId ID de la especie (1=Perro, 2=Gato)
     * @param speciesName Nombre de la especie
     * @return Lista ordenada de entidades para Room (top 10)
     */
    private fun processVaccineRanking(
        applications: List<PetVaccineResponse>,
        speciesId: Int,
        speciesName: String
    ): List<VaccineRankingEntity> {

        // Agrupar por nombre de vacuna y contar
        val vaccineCountMap = applications
            .filter { it.vaccine_type != null && !it.vaccine_type.name.isNullOrBlank() }
            .groupBy { it.vaccine_type.name }
            .mapValues { (_, apps) -> apps.size }

        // Ordenar por cantidad (descendente) y tomar top 10
        return vaccineCountMap
            .entries
            .sortedByDescending { it.value }
            .take(10)
            .mapIndexed { index, (vaccineName, count) ->
                VaccineRankingEntity(
                    id = VaccineRankingEntity.generateId(speciesId, vaccineName),
                    speciesId = speciesId,
                    speciesName = speciesName,
                    vaccineName = vaccineName,
                    count = count,
                    ranking = index + 1,  // 1, 2, 3, ...
                    lastUpdated = System.currentTimeMillis()
                )
            }
    }

    /**
     * Sincroniza todos los datos del dashboard (mascotas + vacunas).
     */
    suspend fun syncAllDashboardData(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {

                // 1. Sincronizar mascotas
                val petsResult = syncAllData()
                if (petsResult.isFailure) {
                }

                // 2. Sincronizar vacunas (IMPORTANTE: llamar explícitamente)
                val vaccinesResult = syncVaccineRankings()
                if (vaccinesResult.isFailure) {
                }

                // Retornar éxito si al menos uno funcionó
                if (petsResult.isSuccess || vaccinesResult.isSuccess) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error al sincronizar dashboard"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción en sincronización completa: ${e.message}", e)
                Result.failure(e)
            }
        }
    }
}