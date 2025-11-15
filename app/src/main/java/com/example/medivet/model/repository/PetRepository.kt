package com.example.medivet.model.repository

import android.content.Context
import android.net.Uri
import com.example.medivet.model.model.PetRequest
import com.example.medivet.model.model.PetResponse
import com.example.medivet.model.model.PetUpdate
import com.example.medivet.model.model.UploadPhotoResponse
import com.example.medivet.model.services.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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

    suspend fun deletePet(petId: Int, token: String): Boolean {
        val response = service.deletePet(petId, "Bearer $token")
        return response.isSuccessful
    }

    suspend fun updatePetPhoto(petId: Int, uri: Uri, token: String, context: Context): UploadPhotoResponse {
        val stream = context.contentResolver.openInputStream(uri)!!
        val bytes = stream.readBytes()
        val requestFile = bytes.toRequestBody("image/*".toMediaType())
        val photoPart = MultipartBody.Part.createFormData("photo", "pet_${petId}.jpg", requestFile)

        return service.updatePetPhoto(petId, photoPart, "Bearer $token")
    }

}