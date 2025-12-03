package com.example.medivet.model.services

import android.content.Context
import com.example.medivet.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private lateinit var retrofit: Retrofit

    fun init(appContext: Context) {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }

        val okHttpClient = OkHttpClient.Builder()
            // Interceptor que agrega automáticamente el token a todas las peticiones
            .addInterceptor(AuthInterceptor(appContext.applicationContext)) // ✅ seguro
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Servicio principal usado por PerfilViewModel y ProfileRepository:
    // login, getUserByEmail, getCurrentUser, uploadProfilePhoto.
    val apiService: AuthService by lazy { retrofit.create(AuthService::class.java) }
    val petService: PetService by lazy { retrofit.create(PetService::class.java) }
    val dashboardService: DashboardApiService by lazy {
        retrofit.create(DashboardApiService::class.java)
    }

    val chatService: ChatService by lazy {
        retrofit.create(ChatService::class.java)
    }
}
