package com.example.medivet.model.model

import com.google.gson.annotations.SerializedName

data class AppointmentResponse(
    val id: Int,
    val date: String,
    val time: String,
    val reason: String,

    @SerializedName("status_id") val statusId: Int,
    @SerializedName("clinic_id") val clinicId: Int,
    @SerializedName("pet_id") val petId: Int,
    @SerializedName("service_id") val serviceId: Int,

)