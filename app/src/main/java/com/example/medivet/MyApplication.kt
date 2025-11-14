package com.example.medivet

import com.example.medivet.model.services.ApiClient
import android.app.Application
import android.content.Context

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializar ApiClient con el contexto de la aplicación
        ApiClient.init(this)
    }
}