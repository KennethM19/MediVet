package com.example.medivet.viewModel.clinic

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.medivet.model.model.AppointmentRequest
import com.example.medivet.model.model.AppointmentResponse
import com.example.medivet.model.model.ClinicResponse
import com.example.medivet.model.model.ServiceResponse
import com.example.medivet.model.repository.ClinicRepository
import com.example.medivet.utils.SessionManager
import com.example.medivet.workers.ReminderWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar

class ClinicViewModel(
    private val repository: ClinicRepository,
    private val sessionManager: SessionManager
) : ViewModel() {


    private val _clinicServices = MutableStateFlow<List<ServiceResponse>>(emptyList())
    val clinicServices: StateFlow<List<ServiceResponse>> = _clinicServices.asStateFlow()

    private val _appointmentCreated = MutableStateFlow<Boolean>(false)
    val appointmentCreated: StateFlow<Boolean> = _appointmentCreated.asStateFlow()

    private val _appointments = MutableStateFlow<List<AppointmentResponse>>(emptyList())
    val appointments: StateFlow<List<AppointmentResponse>> = _appointments.asStateFlow()

    private val _clinics = MutableStateFlow<List<ClinicResponse>>(emptyList())
    val clinics: StateFlow<List<ClinicResponse>> = _clinics.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadClinics()
    }

    fun loadClinics() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = sessionManager.token.first()
                if (token == null) {
                    _error.value = "No autenticado"
                    return@launch
                }

                val response = repository.getClinics(token)
                if (response.isSuccessful && response.body() != null) {
                    _clinics.value = response.body()!!
                } else {
                    _error.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createAppointment(
        context: Context,
        date: String,
        time: String,
        reason: String,
        clinicId: Int,
        serviceId: Int,
        statusId: Int,
        petId: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _appointmentCreated.value = false
            try {
                val token = sessionManager.token.first() ?: return@launch

                val request =
                    AppointmentRequest(date, time, reason, clinicId, serviceId, statusId, petId)
                val response = repository.createAppointment(token, request)

                if (response.isSuccessful) {
                    _appointmentCreated.value = true

                    scheduleNotification(context, date, time, "Recuerda tu cita mañana!")

                } else {
                    _error.value = "Error al agendar: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetAppointmentState() {
        _appointmentCreated.value = false
    }

    fun loadClinicServices(clinicId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = sessionManager.token.first() ?: return@launch
                val response = repository.getClinicServices(token, clinicId)

                if (response.isSuccessful) {
                    _clinicServices.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error al cargar servicios: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMyAppointments() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = sessionManager.token.first() ?: return@launch
                val response = repository.getMyAppointments(token)

                if (response.isSuccessful) {
                    _appointments.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error cargando citas: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun scheduleNotification(context: Context, dateStr: String, timeStr: String, message: String) {
        try {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val dateTimeString = "$dateStr ${timeStr.substring(0, 8)}"
            val appointmentDate = format.parse(dateTimeString) ?: return

            //prueba de funcionalidad
            val delay = 10000L

//            val triggerTime = Calendar.getInstance()
//            triggerTime.time = appointmentDate
//            triggerTime.add(Calendar.HOUR_OF_DAY, -24)
//
//            val now = System.currentTimeMillis()
//            val delay = triggerTime.timeInMillis - now

            if (delay > 0) {
                val data = workDataOf(
                    "title" to "¡Cita Mañana!",
                    "message" to message
                )

                val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .build()

                WorkManager.getInstance(context).enqueue(workRequest)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

