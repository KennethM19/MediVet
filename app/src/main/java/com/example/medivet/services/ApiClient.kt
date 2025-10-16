// services/ApiClient.kt

package com.example.medivet.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.medivet.BuildConfig
import com.example.medivet.services.AuthService

object ApiClient {

    val apiService: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }
}