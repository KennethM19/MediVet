package com.example.medivet.model.services

import com.example.medivet.model.model.PetRequest
import com.example.medivet.model.model.PetResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface PetService {

    @GET("/utils/sex")
    suspend fun getSexOptions(): Response<List<Map<String, Any>>>

    @GET("/utils/species")
    suspend fun getSpeciesOptions(): Response<List<Map<String, Any>>>

    @GET("/utils/breed")
    suspend fun getBreedOptions(): Response<List<Map<String, Any>>>

    @POST("/pets")
    suspend fun createPet(
        @Header("Authorization") token: String,
        @Body pet: PetRequest
    ): Response<PetResponse>

    @GET("/pets")
    suspend fun getPet(@Query("pet_id") petId: Int, @Header("Authorization") token: String): List<PetResponse>


    @GET("/pets/user")
    suspend fun getPets(@Query("user_id") userId: Int): Response<List<PetResponse>>
}
