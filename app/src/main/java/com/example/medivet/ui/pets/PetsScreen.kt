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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.medivet.navigation.AppScreens
import com.example.medivet.ui.components.BottomNavBar
import com.example.medivet.ui.components.PetCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetsScreen(
    navController: NavHostController,
    viewModel: PetsViewModel = viewModel()
) {
    val pets by viewModel.pets.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis mascotas") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF00BFA5), // color de tu app
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(AppScreens.AddPetScreen.route)
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
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            items(pets) { pet ->
                PetCard(
                    pet = pet,
                    onEditClick = {

                        // navController.navigate(AppScreens.EditPetScreen.route + "/${pet.id}")
                    }
                )
            }
        }
    }
}
