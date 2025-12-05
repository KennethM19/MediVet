package com.example.medivet.model.services

import com.example.medivet.model.model.ClinicResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import com.example.medivet.model.model.AppointmentRequest
import com.example.medivet.model.model.AppointmentResponse
import com.example.medivet.model.model.ServiceResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ClinicService {
    @GET("clinics")
    suspend fun getClinics(
        @Header("Authorization") token: String
    ): Response<List<ClinicResponse>>

    @POST("clinics/appointment")
    suspend fun createAppointment(
        @Header("Authorization") token: String,
        @Body request: AppointmentRequest
    ): Response<AppointmentResponse>

    @GET("clinics/{clinic_id}/services")
    suspend fun getClinicServices(
        @Header("Authorization") token: String,
        @Path("clinic_id") clinicId: Int
    ): Response<List<ServiceResponse>>

    @GET("clinics/appointment")
    suspend fun getMyAppointments(
        @Header("Authorization") token: String
    ): Response<List<AppointmentResponse>>

}