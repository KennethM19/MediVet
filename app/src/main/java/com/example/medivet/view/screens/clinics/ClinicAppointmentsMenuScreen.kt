package com.example.medivet.view.screens.clinics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.medivet.view.navigation.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicAppointmentsMenuScreen(
    navController: NavHostController,
    clinicId: Int?,
    serviceId: Int?
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Gestión de Citas", color = Color.White, fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Botón 1: Agendar Cita (Lleva a la pantalla de formulario)
            MenuButton(
                text = "Agendar Cita",
                icon = Icons.Default.EventAvailable,
                color = Color(0xFF4CAF50),
                onClick = {
                    if (clinicId != null) {
                        navController.navigate(AppScreens.ScheduleAppointmentScreen.route + "/$clinicId")
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Botón 4: Mis citas
            MenuButton(
                text = "Mis Citas",
                icon = Icons.Default.DateRange,
                color = Color(0xFF673AB7),
                onClick = {navController.navigate(AppScreens.MyAppointmentsScreen.route)}
            )
        }
    }
}

@Composable
fun MenuButton(text: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        elevation = ButtonDefaults.buttonElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}