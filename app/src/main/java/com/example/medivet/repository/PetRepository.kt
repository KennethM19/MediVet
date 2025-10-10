package com.example.medivet.repository

import com.example.medivet.model.Pet

class PetRepository {

    // Simulación de datos locales (mock)
    fun getUserPets(): List<Pet> {
        return listOf(
            Pet(
                id = "1",
                name = "Luna",
                gender = "Hembra",
                weight = "11.4 kg",
                age = "1 año 4 meses",
                breed = "Sin raza",
                imageUrl = "https://images.unsplash.com/photo-1518717758536-85ae29035b6d"
            ),
            Pet(
                id = "2",
                name = "Rocky",
                gender = "Macho",
                weight = "15.2 kg",
                age = "2 años",
                breed = "Labrador",
                imageUrl = "https://images.unsplash.com/photo-1558788353-f76d92427f16"
            )
        )
    }
}