package com.example.medivet.model.services

import com.example.medivet.model.model.PetResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DashboardApiService {

    /**
     * Obtiene todas las mascotas con filtros opcionales.
     * Usa el endpoint existente GET /pets con query parameters.
     *
     * Para dashboards:
     * - Sin filtros: Obtiene todas las mascotas
     * - Con species_id: Filtra por especie
     * - Con neutered: Filtra por estado de castración
     *
     * El interceptor AuthInterceptor agrega automáticamente el token.
     */
    @GET("pets")
    suspend fun getAllPets(
        @Query("user_id") userId: Int? = null,
        @Query("species_id") speciesId: Int? = null,
        @Query("breed_id") breedId: Int? = null,
        @Query("sex_id") sexId: Int? = null,
        @Query("neutered") neutered: Boolean? = null,
        @Query("name") name: String? = null,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 1000,
        @Query("sort_by") sortBy: String = "name",
        @Query("sort_order") sortOrder: String = "asc"
    ): Response<List<PetResponse>>

}