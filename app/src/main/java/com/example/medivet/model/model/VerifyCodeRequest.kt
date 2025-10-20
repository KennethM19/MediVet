package com.example.medivet.model.model

data class VerifyCodeRequest(
    val email: String,
    val code: String
)
