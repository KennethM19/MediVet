package com.example.medivet.model.services

import com.example.medivet.model.model.ClinicResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ClinicService {
    @GET("clinics")
    suspend fun getClinics(
        @Header("Authorization") token: String
    ): Response<List<ClinicResponse>>
}