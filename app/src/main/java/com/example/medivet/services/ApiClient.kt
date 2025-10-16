// services/ApiClient.kt

package com.example.medivet.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.medivet.BuildConfig
import com.example.medivet.services.AuthService

object ApiClient {

    // 1. Crea una instancia de Retrofit que se pueda reutilizar.
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 2. Usa la instancia de Retrofit para crear tu AuthService.
    val apiService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    // 3. Usa LA MISMA instancia de Retrofit para crear tu PetService.
    val petService: PetService by lazy {
        retrofit.create(PetService::class.java)
    }

}