package com.example.medivet.model

data class VerifyCodeRequest(
    val email: String,
    val code: String
)
