package com.example.medivet.model.model

data class PetsByNeuteredResponse(
    val estado: String,       // "Castrado", "No castrado"
    val cantidad: Int         // Número de mascotas
)
