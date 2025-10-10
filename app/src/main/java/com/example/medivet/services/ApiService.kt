package com.example.medivet.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val access_token: String, val token_type: String)

interface ApiService {
    @POST("auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>
}
