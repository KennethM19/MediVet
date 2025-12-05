package com.example.medivet.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationManager(private val context: Context) {

    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun hasPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED ||
                coarse == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Pair<Double, Double>? =
        suspendCancellableCoroutine { cont ->

            fusedClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    cont.resume(Pair(loc.latitude, loc.longitude))
                    return@addOnSuccessListener
                }

                val req = CurrentLocationRequest.Builder()
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .setMaxUpdateAgeMillis(0)
                    .build()

                fusedClient.getCurrentLocation(req, null)
                    .addOnSuccessListener { loc2 ->
                        if (!cont.isCompleted) {
                            cont.resume(
                                loc2?.let { Pair(it.latitude, it.longitude) }
                            )
                        }
                    }
                    .addOnFailureListener {
                        if (!cont.isCompleted) cont.resume(null)
                    }
            }.addOnFailureListener {
                if (!cont.isCompleted) cont.resume(null)
            }
        }
}