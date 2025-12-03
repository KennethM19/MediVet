package com.example.medivet.model.model

data class PetRequest(
    val id: Int? = null,               // ID de la mascota
    val num_doc: String? = null,      // DNI de la mascota
    val name: String,                 // Nombre
    val photo: String? = null,        // URL o base64 de la foto
    val sex_id: Int,                  // Sexo (macho/hembra)
    val specie_id: Int,               // Especie (perro/gato/etc)
    val year_birth: Int? = null,      // Año de nacimiento
    val month_birth: Int? = null,     // Mes de nacimiento
    val weight: Double? = null,       // Peso en kg
    val neutered: Boolean,            // Si está esterilizado o no
    val breed_id: Int                 // Raza
)
