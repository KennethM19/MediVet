package com.example.medivet.model.repository

import com.example.medivet.model.model.AppointmentRequest
import com.example.medivet.model.model.AppointmentResponse
import com.example.medivet.model.model.ClinicResponse
import com.example.medivet.model.model.ServiceResponse
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

    suspend fun createAppointment(token: String, request: AppointmentRequest): Response<AppointmentResponse> {
        return withContext(Dispatchers.IO) {
            service.createAppointment("Bearer $token", request)
        }
    }

    suspend fun getClinicServices(token: String, clinicId: Int): Response<List<ServiceResponse>> {
        return withContext(Dispatchers.IO) {
            service.getClinicServices("Bearer $token", clinicId)
        }
    }

    suspend fun getMyAppointments(token: String): Response<List<AppointmentResponse>> {
        return withContext(Dispatchers.IO) {
            service.getMyAppointments("Bearer $token")
        }
    }

}