package com.example.medivet.model.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int = 0,
    val type_document_id: Int = 0,
    val num_document: String = "",
    val name: String = "",
    val lastname: String = "",
    val birth_date: String = "",
    val address: String = "",
    val num_cellphone: String? = null,
    val num_telephone: String? = null,
    val email: String = "",     // Usado por ProfileRepository para obtener usuario por email
    val role_id: Int = 0,
    val photo: String? = null,      //Aquí se almacena la URL pública devuelta por Firebase Storage
    val type_document: TypeDocument? = null,
    val role: Role? = null
)

@Serializable
data class TypeDocument(
    val id: Int = 0,
    val name: String = ""
)

@Serializable
data class Role(
    val id: Int = 0,
    val name: String = ""
)
