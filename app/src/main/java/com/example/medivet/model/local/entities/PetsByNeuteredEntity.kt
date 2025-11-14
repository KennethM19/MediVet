package com.example.medivet.model.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets_by_neutered_cache")
data class PetsByNeuteredEntity(
    @PrimaryKey
    val estado: String,
    val cantidad: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)
