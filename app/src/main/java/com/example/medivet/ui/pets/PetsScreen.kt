package com.example.medivet.ui.pets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.medivet.navigation.AppScreens
import com.example.medivet.repository.PetRepository // <-- Importa el repositorio
import com.example.medivet.ui.components.BottomNavBar
import com.example.medivet.ui.components.PetCard
import com.example.medivet.utils.SessionManager // <-- Importa el SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetsScreen(
    navController: NavHostController
) {
    val context = LocalContext.current

    val sessionManager = SessionManager(context)
    val petRepository = PetRepository()

    val factory = PetsViewModelFactory(
        petRepository = petRepository,
        sessionManager = sessionManager
    )

    // 4. Pasa la fábrica a la función viewModel() para que pueda crear el ViewModel
    val viewModel: PetsViewModel = viewModel(factory = factory)

    val pets by viewModel.pets.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis mascotas") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF00BFA5),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(AppScreens.CreatePetScreen.route)
                },
                containerColor = Color(0xFF00BFA5),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar mascota")
            }
        },
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            if (error != null) {
                Text(
                    text = "Error: $error",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
            LazyColumn(
                contentPadding = PaddingValues(16.dp)
            ) {
                items(pets) { pet ->
                    PetCard(
                        pet = pet,
                        onEditClick = {
                            // Lógica para editar
                        }
                    )
                }
            }
        }
    }
}
