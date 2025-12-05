package com.example.medivet.utils

import com.example.medivet.model.model.ClinicResponse
import com.example.medivet.model.repository.ClinicRepository
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class ClosestClinicUseCase(
    private val repository: ClinicRepository
) {

    suspend fun execute(token: String, userLat: Double, userLng: Double): ClinicResponse? {

        val response = repository.getClinics(token)

        if (!response.isSuccessful || response.body().isNullOrEmpty()) {
            return null
        }

        val clinics = response.body()!!

        return clinics.minByOrNull { clinic ->
            distanceInKm(
                userLat,
                userLng,
                clinic.latitude.toDouble(),
                clinic.longitude.toDouble()
            )
        }
    }

    private fun distanceInKm(
        userLat: Double,
        userLng: Double,
        clinicLat: Double,
        clinicLng: Double
    ): Double {

        val R = 6371
        val dLat = Math.toRadians(clinicLat - userLat)
        val dLon = Math.toRadians(clinicLng - userLng)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(userLat)) *
                cos(Math.toRadians(clinicLat)) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }
}