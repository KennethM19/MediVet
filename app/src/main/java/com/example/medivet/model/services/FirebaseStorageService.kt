package com.example.medivet.model.services

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.medivet.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.concurrent.TimeUnit

class FirebaseStorageService(private val context: Context) {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val baseUrl = BuildConfig.BASE_URL

    suspend fun uploadUserProfilePhoto(
        uri: Uri,
        token: String
    ): String? = withContext(Dispatchers.IO) {
        try {

            // Convierte la Uri seleccionada por el usuario en un archivo físico temporal.
            // Este archivo es necesario para enviar la imagen como multipart al backend.
            val file = uriToFile(uri)

            // // Prepara el cuerpo de la imagen con tipo "image/*"
            val requestBody = file.asRequestBody("image/*".toMediaType())

            // Crear la solicitud multipart
            // Este formato coincide exactamente con lo que espera el endpoint upload-photo.
            val multipartBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("photo", file.name, requestBody)
                .build()

            // Construye la petición POST hacia /users/upload-photo
            val request = Request.Builder()
                .url("$baseUrl/users/upload-photo")
                .addHeader(
                    "Authorization",
                    "Bearer $token"
                )    // Token necesario para validar usuario
                .post(multipartBody)
                .build()

            // Se envía la solicitud al backend, que sube la foto a Firebase Storage
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()

                // Extrae la URL pública devuelta por el backend
                // Esta URL es la que luego se guarda en BD y se muestra en el perfil del usuario
                val photoUrl = extractPhotoUrlFromResponse(responseBody)

                // El archivo temporal se borra después de enviarlo al backend
                file.delete()

                if (photoUrl != null) {
                } else {
                }

                photoUrl
            } else {
                val errorBody = response.body?.string()
                Log.e(
                    "FirebaseStorageService",
                    "Error del servidor - Código: ${response.code}, Mensaje: ${response.message}, Cuerpo: $errorBody"
                )
                file.delete()
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseStorageService", "Excepción al subir foto: ${e.javaClass.simpleName}", e)
            e.printStackTrace()
            null
        }
    }

    // Convierte la Uri de la foto seleccionada a un archivo temporal.
    // Esto es requerido para poder enviarla al backend como un MultipartBody.Part.
    private suspend fun uriToFile(uri: Uri): File = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalStateException("No se pudo abrir el stream de entrada")

            val file = File(context.cacheDir, "temp_photo_${System.currentTimeMillis()}.jpg")

            inputStream.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            Log.d("FirebaseStorageService", "Archivo creado exitosamente: ${file.absolutePath}")
            file
        } catch (e: Exception) {
            Log.e("FirebaseStorageService", "Error al convertir Uri a File: ${e.message}", e)
            throw e
        }
    }

    // Extrae la URL devuelta por el backend desde el JSON.
    // El backend puede responder como "url" o "photo_url", por eso se manejan ambos casos.
    private fun extractPhotoUrlFromResponse(responseBody: String?): String? {
        return try {
            if (responseBody == null) {
                return null
            }

            // Busca el campo principal "url" devuelto por el backend
            val photoUrlPattern = """"url"\s*:\s*"([^"]*)"""".toRegex()
            val matchResult = photoUrlPattern.find(responseBody)

            val photoUrl = matchResult?.groupValues?.get(1)

            if (photoUrl.isNullOrEmpty()) {
                // Alternativa por si el backend responde con "photo_url"
                val altPattern = """"photo_url"\s*:\s*"([^"]*)"""".toRegex()
                val altMatch = altPattern.find(responseBody)
                altMatch?.groupValues?.get(1)
            } else {
                photoUrl
            }
        } catch (e: Exception) {
            null
        }
    }
}