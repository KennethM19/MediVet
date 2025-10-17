package com.example.medivet.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medivet.repository.UserRepository
import com.example.medivet.services.ApiClient
import com.example.medivet.model.RegisterRequest
import com.example.medivet.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

// -----------------------------------------------------------
// A. Mapeo de Tipos de Documento (Lógica de Negocio)
// -----------------------------------------------------------

private fun mapDocTypeToId(docType: String): Int {
    return when (docType) {
        "DNI" -> 1
        "Pasaporte" -> 2
        else -> 1
    }
}

// 👈 Función Defensiva para convertir campos opcionales a null
private fun String.toNullIfBlank(): String? {
    return if (this.isBlank()) null else this.trim()
}

// -----------------------------------------------------------
// B. Estados y Data Holder
// -----------------------------------------------------------

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val token: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

data class RegistrationData(
    val docType: String = "",
    val docNumber: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val address: String = "",
    val birthDate: String = "",
    val cellphoneNum: String = "",
    val telephoneNum: String = "",
    val email: String = "",
    val password: String = ""
)

// -----------------------------------------------------------
// C. ViewModel
// -----------------------------------------------------------
class RegisterViewModel(
    private val repository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _registrationData = MutableStateFlow(RegistrationData())
    val registrationData: StateFlow<RegistrationData> = _registrationData.asStateFlow()

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    // --------------------------------------------------
    // I. MÉTODOS DE MANEJO DE ESTADO EN VIVO (SETTERS)
    // --------------------------------------------------
    // Usados directamente por los TextField de RegisterFirstScreen

    fun setDocType(value: String) {
        _registrationData.value = _registrationData.value.copy(docType = value)
    }
    fun setDocNumber(value: String) {
        _registrationData.value = _registrationData.value.copy(docNumber = value)
    }
    fun setFirstName(value: String) {
        _registrationData.value = _registrationData.value.copy(firstName = value)
    }
    fun setLastName(value: String) {
        _registrationData.value = _registrationData.value.copy(lastName = value)
    }
    fun setAddress(value: String) {
        _registrationData.value = _registrationData.value.copy(address = value)
    }
    fun setBirthDate(value: String) {
        _registrationData.value = _registrationData.value.copy(birthDate = value)
    }
    fun setCellphoneNum(value: String) {
        _registrationData.value = _registrationData.value.copy(cellphoneNum = value)
    }
    fun setTelephoneNum(value: String) {
        _registrationData.value = _registrationData.value.copy(telephoneNum = value)
    }
    fun setEmail(value: String) {
        _registrationData.value = _registrationData.value.copy(email = value)
    }
    fun setPassword(value: String) {
        _registrationData.value = _registrationData.value.copy(password = value)
    }

    // --------------------------------------------------
    // II. LÓGICA DE REGISTRO (Llamado desde RegisterSecondScreen)
    // --------------------------------------------------

    fun registerUser() { // Ya no necesita parámetros, lee el estado interno
        val finalData = _registrationData.value // Leemos el estado ya actualizado

        if (finalData.email.isBlank() || finalData.password.isBlank()) {
            _registerState.value = RegisterState.Error("Email y contraseña son obligatorios.")
            return
        }

        // 2. CREAR LA SOLICITUD con los 11 campos exactos del backend
        val request = RegisterRequest(
            type_document_id = mapDocTypeToId(finalData.docType),
            role_id = 1,

            // Aplicamos .trim() a los obligatorios y el mapeo defensivo a los opcionales
            name = finalData.firstName.trim(),
            lastname = finalData.lastName.trim(),
            num_document = finalData.docNumber.trim(),
            address = finalData.address.trim(),
            birth_date = finalData.birthDate.trim(), // Aquí debes asegurar el formato YYYY-MM-DD

            num_cellphone = finalData.cellphoneNum.toNullIfBlank(), // Usamos la función defensiva
            num_telephone = finalData.telephoneNum.toNullIfBlank(), // Usamos la función defensiva

            email = finalData.email.trim(),
            password = finalData.password
        )

        _registerState.value = RegisterState.Loading
        viewModelScope.launch {
            try {
                repository.registerWithFastApi(request)
                // Guardamos el token inicial después del registro exitoso
                sessionManager.saveAuthData(request.email, "FastAPI")
                _registerState.value = RegisterState.Success("REGISTRO_COMPLETADO")
            } catch (e: Exception) {
                Log.e("RegisterVM", "Error de registro: ${e.message}")
                _registerState.value = RegisterState.Error(e.message ?: "Error desconocido al registrar.")
            }
        }
    }

    // --------------------------------------------------
    // III. LÓGICA DE VERIFICACIÓN (Llamado desde AuthenticationScreen)
    // --------------------------------------------------
    // ... (Tu código para verifyCode queda aquí) ...

    fun resetState() {
        _registerState.value = RegisterState.Idle
    }
}