package com.example.medivet.model.repository

import com.example.medivet.model.model.ClinicResponse
import com.example.medivet.model.services.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class ClinicRepository {
    private val service = ApiClient.clinicService

    suspend fun getClinics(token: String): Response<List<ClinicResponse>> {
        return withContext(Dispatchers.IO) {
            service.getClinics("Bearer $token")
        }
    }
}