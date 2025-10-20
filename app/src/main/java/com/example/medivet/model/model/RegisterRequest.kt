package com.example.medivet.model.model

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