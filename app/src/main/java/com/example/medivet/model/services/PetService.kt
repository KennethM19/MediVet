package com.example.medivet.model.services

import com.example.medivet.model.model.PetRequest
import com.example.medivet.model.model.PetResponse
import com.example.medivet.model.model.PetUpdate
import com.example.medivet.model.model.UploadPhotoResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
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
    @DELETE("/pets")
    suspend fun deletePet(
        @Query("pet_id") petId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    @GET("/pets")
    suspend fun getPets(@Query("user_id") userId: Int): Response<List<PetResponse>>

    @PUT("/pets")
    suspend fun updatePet(
        @Query("pet_id") petId: Int,
        @Body petUpdate: PetUpdate,
        @Header("Authorization") token: String
    ): PetResponse

    @Multipart
    @PUT("/pets/update-photo")
    suspend fun updatePetPhoto(
        @Query("pet_id") petId: Int,
        @Part photo: MultipartBody.Part,
        @Header("Authorization") token: String
    ): UploadPhotoResponse
}
