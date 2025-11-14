package com.example.medivet.model.model

data class PetsBySpeciesResponse(
    val especie: String,      // "Perro", "Gato", etc.
    val cantidad: Int         // Número de mascotas
)