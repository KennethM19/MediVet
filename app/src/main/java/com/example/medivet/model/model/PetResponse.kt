package com.example.medivet.model.model

data class PetResponse(
    val id: Int,
    val name: String,
    val year_birth: Int,
    val month_birth: Int,
    val weight: Double,
    val photo: String?,
    val neutered: Boolean,
    val age: AgeResponse,
    val user: UserResponse,
    val breed: BreedResponse?,
    val species: SpeciesResponse?,
    val sex: SexResponse?
)

data class AgeResponse(
    val years: Int,
    val months: Int
)

data class UserResponse(
    val id: Int,
    val name: String,
    val lastname: String,
    val email: String,
    val photo: String?
)

data class BreedResponse(
    val id: Int,
    val name: String
)

data class SpeciesResponse(
    val id: Int,
    val name: String
)

data class SexResponse(
    val id: Int,
    val name: String
)

data class PetUpdate(
    val weight: Double?,
    val neutered: Boolean?,
    val photo: String?
)

data class UploadPhotoResponse(
    val message: String,
    val url: String
)
