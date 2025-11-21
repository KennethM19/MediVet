package com.example.medivet.model.model

import com.google.gson.annotations.SerializedName

/**
 * Respuesta del backend para aplicaciones de vacunas.
 * Endpoint: GET /medical-record/pet-vaccine?specie_id=1
 */
data class PetVaccineResponse(
    val id: Int,
    val pet_id: Int,
    val vaccine_type_id: Int,
    val application_date: String?,
    val vaccine_type: VaccineType
)

/**
 * Tipo de vacuna anidado en la respuesta.
 */
data class VaccineType(
    val id: Int,
    @SerializedName("type")
    val name: String,
    val specie_id: Int
)

/**
 * Modelo genérico para gráficos de vacunas.
 * (Se mantiene igual)
 */
data class VaccineChartData(
    val vaccineName: String,
    val count: Float,
    val color: androidx.compose.ui.graphics.Color? = null
)