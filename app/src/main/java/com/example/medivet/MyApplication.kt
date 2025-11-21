package com.example.medivet

import android.app.Application
import com.example.medivet.model.services.ApiClient

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializar ApiClient con el contexto de la aplicación
        ApiClient.init(this)
    }
}