package com.example.medivet.services

import com.example.medivet.model.AuthRequest
import com.example.medivet.model.AuthResponse
import com.example.medivet.model.RegisterRequest
import com.example.medivet.model.VerifyCodeRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.medivet.model.VerifyResponse

interface AuthService {
    @POST("auth/login")
    suspend fun loginUser(@Body request: AuthRequest): Response<AuthResponse>

    //FUNCIÓN DE REGISTRO
    @POST("/users/")
    suspend fun registerUser(@Body request: RegisterRequest): Response<AuthResponse>

        @POST("/auth/verify")
        suspend fun verifyCode(@Body request: VerifyCodeRequest): Response<VerifyResponse>
}