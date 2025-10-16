package com.example.medivet.model

data class AuthResponse(
    val access_token: String,
    val token_type: String
    // Si tu FastAPI devolviera el User, lo añadirías aquí: val user: User
)