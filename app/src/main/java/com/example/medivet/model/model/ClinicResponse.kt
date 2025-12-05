package com.example.medivet.model.model

data class ClinicResponse(
    val id: Int,
    val ruc: String,
    val name: String,
    val address: String,
    val district: String,
    val province: String,
    val phone: String,
    val latitude: String,
    val longitude: String,
    val webPage: String,
    val service: ServiceData?
)

data class ServiceData(
    val id: Int,
    val name: String,
    val description: String
)