package com.example.medivet.model.services

import com.example.medivet.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    val petService: PetService by lazy {
        retrofit.create(PetService::class.java)
    }

}