package com.example.medivet.model.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad para cachear datos de mascotas por especie
 */
@Entity(tableName = "pets_by_species_cache")
data class PetsBySpeciesEntity(
    @PrimaryKey
    val especie: String,
    val cantidad: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)
