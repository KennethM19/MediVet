package com.example.medivet.model.local.dao

import androidx.room.*
import com.example.medivet.model.local.entities.PetsByNeuteredEntity
import com.example.medivet.model.local.entities.PetsBySpeciesEntity
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

    //  Utilidades

    @Query("SELECT lastUpdated FROM pets_by_species_cache LIMIT 1")
    suspend fun getLastUpdateTime(): Long?

    @Query("DELETE FROM pets_by_species_cache")
    suspend fun clearAllSpeciesData()

    @Query("DELETE FROM pets_by_neutered_cache")
    suspend fun clearAllNeuteredData()
}