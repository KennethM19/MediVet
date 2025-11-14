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
            Log.d("FirebaseStorageService", "Iniciando carga de foto...")

            // Convertir Uri a File en el hilo de IO
            val file = uriToFile(uri)
            Log.d("FirebaseStorageService", "Archivo temporal creado: ${file.absolutePath}")

            // Crear MultipartBody.Part para la foto
            val requestBody = file.asRequestBody("image/*".toMediaType())
            Log.d("FirebaseStorageService", "Tamaño del archivo: ${file.length()} bytes")

            // Crear la solicitud multipart
            val multipartBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("photo", file.name, requestBody)
                .build()

            val request = Request.Builder()
                .url("$baseUrl/users/upload-photo")
                .addHeader("Authorization", "Bearer $token")
                .post(multipartBody)
                .build()

            Log.d("FirebaseStorageService", "Enviando solicitud a: $baseUrl/users/upload-photo")

            // Ejecutar la solicitud en el hilo de IO
            val response = client.newCall(request).execute()

            Log.d("FirebaseStorageService", "Código de respuesta: ${response.code}")

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("FirebaseStorageService", "Respuesta del servidor: $responseBody")

                val photoUrl = extractPhotoUrlFromResponse(responseBody)

                // Limpiar archivo temporal
                file.delete()
                Log.d("FirebaseStorageService", "Archivo temporal eliminado")

                if (photoUrl != null) {
                    Log.d("FirebaseStorageService", "URL de foto extraída: $photoUrl")
                } else {
                    Log.e("FirebaseStorageService", "No se pudo extraer URL de la respuesta")
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

    private fun extractPhotoUrlFromResponse(responseBody: String?): String? {
        return try {
            if (responseBody == null) {
                Log.e("FirebaseStorageService", "Cuerpo de respuesta es nulo")
                return null
            }

            Log.d("FirebaseStorageService", "Parseando respuesta: $responseBody")

            // Intentar extraer la URL buscando "url" (que es lo que devuelve el backend)
            val photoUrlPattern = """"url"\s*:\s*"([^"]*)"""".toRegex()
            val matchResult = photoUrlPattern.find(responseBody)

            val photoUrl = matchResult?.groupValues?.get(1)

            if (photoUrl.isNullOrEmpty()) {
                Log.w("FirebaseStorageService", "URL no encontrada en la respuesta: $responseBody")
                // Intentar con el patrón alternativo por si acaso
                val altPattern = """"photo_url"\s*:\s*"([^"]*)"""".toRegex()
                val altMatch = altPattern.find(responseBody)
                altMatch?.groupValues?.get(1)
            } else {
                Log.d("FirebaseStorageService", "URL encontrada exitosamente: $photoUrl")
                photoUrl
            }
        } catch (e: Exception) {
            Log.e("FirebaseStorageService", "Error al parsear respuesta JSON: ${e.message}", e)
            null
        }
    }
}