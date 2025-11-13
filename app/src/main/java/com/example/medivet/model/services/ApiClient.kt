package com.example.medivet.model.services

import com.example.medivet.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object ApiClient {

    private lateinit var context: Context

    // Método para inicializar desde Application
    fun init(appContext: Context) {
        context = appContext.applicationContext
    }

    private val okHttpClient: OkHttpClient by lazy {
        // Interceptor para logs (solo en modo debug)
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context)) // Agrega el token automáticamente
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
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