package com.example.medivet.model

data class PetResponse(
    val id: Int,
    val user_id: Int,
    val num_doc: String?,
    val name: String,
    val photo: String?,
    val sex_id: Int,
    val specie_id: Int,
    val year_birth: Int?,
    val month_birth: Int?,
    val weight: Double?,
    val neutered: Boolean,
    val breed_id: Int
)
