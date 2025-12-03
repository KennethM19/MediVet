package com.example.medivet.model.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.medivet.model.local.entities.PetsByNeuteredEntity
import com.example.medivet.model.local.entities.PetsBySpeciesEntity
import com.example.medivet.model.local.entities.VaccineRankingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DashboardDao {

    // Mascotas por especie

    @Query("SELECT * FROM pets_by_species_cache")
    fun getPetsBySpecies(): Flow<List<PetsBySpeciesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPetsBySpecies(data: List<PetsBySpeciesEntity>)

    @Query("DELETE FROM pets_by_species_cache")
    suspend fun clearPetsBySpecies()

    @Transaction
    suspend fun replacePetsBySpecies(data: List<PetsBySpeciesEntity>) {
        clearPetsBySpecies()
        insertPetsBySpecies(data)
    }

    //  Mascotas por castración

    @Query("SELECT * FROM pets_by_neutered_cache")
    fun getPetsByNeutered(): Flow<List<PetsByNeuteredEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPetsByNeutered(data: List<PetsByNeuteredEntity>)

    @Query("DELETE FROM pets_by_neutered_cache")
    suspend fun clearPetsByNeutered()

    @Transaction
    suspend fun replacePetsByNeutered(data: List<PetsByNeuteredEntity>) {
        clearPetsByNeutered()
        insertPetsByNeutered(data)
    }

    // ========== Vacunas por especie ==========

    /**
     * Obtiene el ranking de vacunas para una especie específica.
     * @param speciesId 1 = Perro, 2 = Gato
     */
    @Query("SELECT * FROM vaccine_ranking_cache WHERE speciesId = :speciesId ORDER BY ranking ASC")
    fun getVaccineRankingBySpecies(speciesId: Int): Flow<List<VaccineRankingEntity>>

    /**
     * Inserta o actualiza el ranking de vacunas.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaccineRanking(data: List<VaccineRankingEntity>)

    /**
     * Elimina el ranking de una especie específica.
     */
    @Query("DELETE FROM vaccine_ranking_cache WHERE speciesId = :speciesId")
    suspend fun clearVaccineRankingBySpecies(speciesId: Int)

    /**
     * Reemplaza todo el ranking de una especie (transacción atómica).
     */
    @Transaction
    suspend fun replaceVaccineRankingBySpecies(speciesId: Int, data: List<VaccineRankingEntity>) {
        clearVaccineRankingBySpecies(speciesId)
        insertVaccineRanking(data)
    }

    /**
     * Obtiene la última actualización del ranking de vacunas.
     */
    @Query("SELECT lastUpdated FROM vaccine_ranking_cache WHERE speciesId = :speciesId LIMIT 1")
    suspend fun getVaccineRankingLastUpdate(speciesId: Int): Long?

    //  Utilidades

    @Query("SELECT lastUpdated FROM pets_by_species_cache LIMIT 1")
    suspend fun getLastUpdateTime(): Long?

    @Query("DELETE FROM pets_by_species_cache")
    suspend fun clearAllSpeciesData()

    @Query("DELETE FROM pets_by_neutered_cache")
    suspend fun clearAllNeuteredData()
}