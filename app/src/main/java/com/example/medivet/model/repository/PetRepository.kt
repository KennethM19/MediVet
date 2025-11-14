package com.example.medivet.model.repository

import com.example.medivet.model.model.PetRequest
import com.example.medivet.model.model.PetResponse
import com.example.medivet.model.model.PetUpdate
import com.example.medivet.model.services.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class PetRepository {

    private val service = ApiClient.petService

    suspend fun getSexOptions() = withContext(Dispatchers.IO) { service.getSexOptions() }
    suspend fun getSpeciesOptions() = withContext(Dispatchers.IO) { service.getSpeciesOptions() }
    suspend fun getBreedOptions() = withContext(Dispatchers.IO) { service.getBreedOptions() }

    suspend fun createPet(token: String, pet: PetRequest): Response<PetResponse> {
        return withContext(Dispatchers.IO) {
            service.createPet("Bearer $token", pet)
        }
    }

    suspend fun getPet(petId: Int, token: String): PetResponse? {
        val pets = ApiClient.petService.getPet(petId, "Bearer $token")
        return pets.firstOrNull()
    }

    suspend fun getPets(userId: Int): Response<List<PetResponse>> {
        return withContext(Dispatchers.IO) {
            service.getPets(userId)
        }
    }

    suspend fun updatePet(petId: Int, update: PetUpdate, token: String): PetResponse {
        return service.updatePet(petId, update, "Bearer $token")
    }

}