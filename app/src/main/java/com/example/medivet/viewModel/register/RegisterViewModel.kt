package com.example.medivet.viewModel.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medivet.model.model.VerifyCodeRequest
import com.example.medivet.model.model.RegisterRequest
import com.example.medivet.model.repository.UserRepository
import com.example.medivet.model.services.ApiClient
import com.example.medivet.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private fun mapDocTypeToId(docType: String): Int {
    return when (docType) {
        "DNI" -> 1
        "Pasaporte" -> 2
        else -> 1
    }
}

private fun String.toNullIfBlank(): String? {
    return if (this.isBlank()) null else this.trim()
}

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

class RegisterViewModel(
    private val repository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _registrationData = MutableStateFlow(RegistrationData())
    val registrationData: StateFlow<RegistrationData> = _registrationData.asStateFlow()

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

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

    fun registerUser() {
        val finalData = _registrationData.value

        if (finalData.email.isBlank() || finalData.password.isBlank()) {
            _registerState.value = RegisterState.Error("Email y contraseña son obligatorios.")
            return
        }

        val request = RegisterRequest(
            type_document_id = mapDocTypeToId(finalData.docType),
            role_id = 1,

            name = finalData.firstName.trim(),
            lastname = finalData.lastName.trim(),
            num_document = finalData.docNumber.trim(),
            address = finalData.address.trim(),
            birth_date = finalData.birthDate.trim(),

            num_cellphone = finalData.cellphoneNum.toNullIfBlank(),
            num_telephone = finalData.telephoneNum.toNullIfBlank(),

            email = finalData.email.trim(),
            password = finalData.password
        )

        _registerState.value = RegisterState.Loading
        viewModelScope.launch {
            try {
                repository.registerWithFastApi(request)

                sessionManager.saveAuthData(request.email, "FastAPI")
                _registerState.value = RegisterState.Success("REGISTRO_COMPLETADO")
            } catch (e: Exception) {
                Log.e("RegisterVM", "Error de registro: ${e.message}")
                _registerState.value =
                    RegisterState.Error(e.message ?: "Error desconocido al registrar.")
            }
        }
    }


    fun resetState() {
        _registerState.value = RegisterState.Idle
    }

    fun verifyCode(authCode: String) {
        val email = _registrationData.value.email
        if (email.isBlank()) {
            _registerState.value = RegisterState.Error("Email no disponible para verificar.")
            return
        }

        val request = VerifyCodeRequest(
            email = email,
            code = authCode
        )

        _registerState.value = RegisterState.Loading
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.verifyCode(request)
                if (response.isSuccessful) {
                    _registerState.value = RegisterState.Success("VERIFICACION_EXITOSA")
                } else {
                    _registerState.value = RegisterState.Error("Código incorrecto o expirado")
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(e.message ?: "Error desconocido")
            }
        }
    }


}