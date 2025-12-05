package com.example.medivet.model.model

import com.google.gson.annotations.SerializedName

data class AppointmentRequest(
    val date: String,       // Formato "YYYY-MM-DD"
    val time: String,       // Formato "HH:MM:SS"
    val reason: String,

    @SerializedName("clinic_id")
    val clinicId: Int,

    @SerializedName("service_id")
    val serviceId: Int,

    @SerializedName("status_id")
    val statusId: Int,

    @SerializedName("pet_id")
    val petId: Int
)