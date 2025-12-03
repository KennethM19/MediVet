package com.example.medivet.model.services

import com.example.medivet.model.model.ChatRequest
import com.example.medivet.model.model.ChatResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ChatService {
    @POST("chat")
    suspend fun sendMessage(
        @Body request: ChatRequest,
        @Header("Authorization") token: String
    ): ChatResponse
}