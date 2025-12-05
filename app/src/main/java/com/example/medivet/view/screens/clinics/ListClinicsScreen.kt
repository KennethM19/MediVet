package com.example.medivet.view.screens.clinics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable // Importante para el click
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.medivet.ui.components.BottomNavBar
import com.example.medivet.ui.components.ClinicCard
import com.example.medivet.view.navigation.AppScreens
import com.example.medivet.viewModel.clinic.ClinicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListClinicsScreen(
    navController: NavHostController,
    viewModel: ClinicViewModel
) {


    val clinics by viewModel.clinics.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Veterinarias", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF00BFA5))
            )
        },
        bottomBar = { BottomNavBar(navController = navController) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF0F0F0))
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null) {
                Text(
                    text = "Error: $error",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (clinics.isEmpty()) {
                Text(
                    text = "No hay veterinarias disponibles.",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(clinics) { clinic ->
                        // Envolvemos la tarjeta en una Box clickeable para navegar
                        Box(modifier = Modifier.clickable {
                            // --- NAVEGACIÓN AL DETALLE ---
                            navController.navigate(AppScreens.ClinicScreen.route + "/${clinic.id}")
                        }) {
                            ClinicCard(clinic = clinic)
                        }
                    }
                }
            }
        }
    }
}