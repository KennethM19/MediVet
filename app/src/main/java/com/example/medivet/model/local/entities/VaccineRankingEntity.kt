package com.example.medivet.model.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad para cachear ranking de vacunas por especie.
 * Usa una clave primaria compuesta (vacuna + especie).
 */
@Entity(tableName = "vaccine_ranking_cache")
data class VaccineRankingEntity(
    @PrimaryKey
    val id: String,                    // Compuesto: "especie_1_Rabia"
    val speciesId: Int,                // 1 = Perro, 2 = Gato
    val speciesName: String,           // "Perro" o "Gato"
    val vaccineName: String,           // "Rabia", "Parvovirus", etc.
    val count: Int,                    // Número de aplicaciones
    val ranking: Int,                  // Posición en el ranking (1, 2, 3...)
    val lastUpdated: Long = System.currentTimeMillis()
) {
    companion object {
        fun generateId(speciesId: Int, vaccineName: String): String {
            return "species_${speciesId}_${vaccineName}"
        }
    }
}
