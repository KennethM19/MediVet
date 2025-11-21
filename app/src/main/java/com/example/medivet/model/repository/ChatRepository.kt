package com.example.medivet.model.repository

import com.example.medivet.model.model.ChatRequest
import com.example.medivet.model.model.ChatResponse
import com.example.medivet.model.services.ApiClient

class ChatRepository {

    val service = ApiClient.chatService

    suspend fun sendMessage(message: ChatRequest, token: String) : ChatResponse {
        return service.sendMessage(message, token)
    }

}