package com.example.medivet.model.services

import com.example.medivet.model.model.AuthRequest
import com.example.medivet.model.model.AuthResponse
import com.example.medivet.model.model.RegisterRequest
import com.example.medivet.model.model.VerifyCodeRequest
import com.example.medivet.model.model.VerifyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun loginUser(@Body request: AuthRequest): Response<AuthResponse>

    //FUNCIÓN DE REGISTRO
    @POST("/users/")
    suspend fun registerUser(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("/auth/verify")
    suspend fun verifyCode(@Body request: VerifyCodeRequest): Response<VerifyResponse>
}