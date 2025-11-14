package com.example.medivet.model.repository

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class ChatRepository(private val client: OkHttpClient) {

    fun sendMessage(userMessage: String): String {

        val jsonBody = """{"message":"$userMessage"}"""
        val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("https://medivet-backend.onrender.com/chat")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            return if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: "{}"
                JSONObject(responseBody).getString("response")
            } else {
                "Error al conectar con el backend"
            }
        }
    }

}