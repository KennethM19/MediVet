package com.example.medivet.view.screens.clinics

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.medivet.viewModel.clinic.ClinicViewModel
import com.example.medivet.viewModel.pet.PetsViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleAppointmentScreen(
    navController: NavHostController,
    clinicId: Int?,
    clinicViewModel: ClinicViewModel,
    petsViewModel: PetsViewModel
) {
    val context = LocalContext.current

    LaunchedEffect(clinicId) {
        if (clinicId != null) {
            clinicViewModel.loadClinicServices(clinicId)
        }
    }

    // Estados del formulario
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var selectedPetId by remember { mutableStateOf<Int?>(null) }
    var selectedPetName by remember { mutableStateOf("Selecciona tu mascota") }
    var petDropdownExpanded by remember { mutableStateOf(false) }
    var serviceDropdownExpanded by remember {mutableStateOf(false)}
    var selectedServiceName by remember{mutableStateOf("Selecciona el servicio requerido")}
    var selectedServiceId by remember{mutableStateOf<Int?>(null)}

    // Observamos datos
    val pets by petsViewModel.pets.collectAsState()
    val services by clinicViewModel.clinicServices.collectAsState()
    val isLoading by clinicViewModel.isLoading.collectAsState()
    val appointmentCreated by clinicViewModel.appointmentCreated.collectAsState()
    val error by clinicViewModel.error.collectAsState()

    // Calendario para los pickers
    val calendar = Calendar.getInstance()

    // Navegar atrás si se crea
    LaunchedEffect(appointmentCreated) {
        if (appointmentCreated) {
            Toast.makeText(context, "¡Cita agendada con éxito!", Toast.LENGTH_LONG).show()
            clinicViewModel.resetAppointmentState()
            navController.popBackStack()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Agendar Cita", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF00BFA5)),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF0F0F0))
                .padding(16.dp)
        ) {

            // 1. Selector de Fecha
            OutlinedTextField(
                value = date,
                onValueChange = {},
                label = { Text("Fecha") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = {
                        DatePickerDialog(context, { _, year, month, day ->
                            // Formato YYYY-MM-DD para el backend
                            val formattedMonth = (month + 1).toString().padStart(2, '0')
                            val formattedDay = day.toString().padStart(2, '0')
                            date = "$year-$formattedMonth-$formattedDay"
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                    }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Fecha")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Selector de Hora
            OutlinedTextField(
                value = time,
                onValueChange = {},
                label = { Text("Hora") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = {
                        TimePickerDialog(context, { _, hour, minute ->
                            // Formato HH:MM:SS
                            val formattedHour = hour.toString().padStart(2, '0')
                            val formattedMinute = minute.toString().padStart(2, '0')
                            time = "$formattedHour:$formattedMinute:00"
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
                    }) {
                        Icon(Icons.Default.AccessTime, contentDescription = "Hora")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = serviceDropdownExpanded,
                onExpandedChange = { serviceDropdownExpanded = !serviceDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedServiceName.ifEmpty { "Selecciona un servicio" },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Servicio") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceDropdownExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = serviceDropdownExpanded,
                    onDismissRequest = { serviceDropdownExpanded = false }
                ) {
                    if (services.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No hay servicios disponibles") },
                            onClick = { serviceDropdownExpanded = false }
                        )
                    } else {
                        services.forEach { service ->
                            DropdownMenuItem(
                                text = { Text(service.name) },
                                onClick = {
                                    selectedServiceName = service.name
                                    selectedServiceId = service.id
                                    serviceDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Selector de Mascota
            ExposedDropdownMenuBox(
                expanded = petDropdownExpanded,
                onExpandedChange = { petDropdownExpanded = !petDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedPetName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Mascota") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = petDropdownExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = petDropdownExpanded,
                    onDismissRequest = { petDropdownExpanded = false }
                ) {
                    pets.forEach { pet ->
                        DropdownMenuItem(
                            text = { Text(pet.name) },
                            onClick = {
                                selectedPetName = pet.name
                                selectedPetId = pet.id
                                petDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Motivo
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Motivo de la consulta") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Botón Agendar
            Button(
                onClick = {
                    if (date.isNotBlank() && time.isNotBlank() && selectedPetId != null &&
                        clinicId != null && selectedServiceId != null) {
                        clinicViewModel.createAppointment(
                            context = context,
                            date = date,
                            time = time,
                            reason = reason,
                            clinicId = clinicId,
                            serviceId = selectedServiceId!!,
                            statusId = 1, // 1 = Pendiente/Confirmada por defecto
                            petId = selectedPetId!!
                        )
                    } else {
                        Toast.makeText(context, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFA5)),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Confirmar Cita", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = error!!, color = Color.Red)
            }
        }
    }
}