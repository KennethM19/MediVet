package com.example.medivet.model

/**
 * Modelo de datos para enviar la solicitud de registro a FastAPI.
 * Coincide exactamente con los 11 campos esperados por el backend.
 */
data class RegisterRequest(
    val type_document_id: Int,
    val num_document: String,
    val name: String,
    val lastname: String,
    val birth_date: String,
    val address: String,
    val num_cellphone: String?,
    val num_telephone: String?,
    val email: String,
    val role_id: Int,
    val password: String
)