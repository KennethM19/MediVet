package com.example.medivet.view.screens.pets

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.medivet.ui.components.BottomNavBar
import com.example.medivet.ui.components.PetCard
import com.example.medivet.utils.SessionManager
import com.example.medivet.view.navigation.AppScreens
import com.example.medivet.viewModel.pet.PetsViewModel
import com.example.medivet.viewModel.pet.PetsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListPetsScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    val factory = PetsViewModelFactory(
        sessionManager = sessionManager
    )

    val viewModel: PetsViewModel = viewModel(factory = factory)

    val pets by viewModel.pets.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mis Mascotas",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF00BFA5)
                )
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AppScreens.CreatePetScreen.route) },
                containerColor = Color(0xFF00BFA5)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Mascota", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF0F0F0))
        ) {
            if (error != null) {
                Text(
                    text = error!!,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
            if (pets.isEmpty() && error == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aún no tienes mascotas registradas. ¡Añade una!",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(pets) { pet ->
                        PetCard(
                            pet = pet,
                            onClick = {
                                navController.navigate(AppScreens.PetScreen.route + "/${pet.id}")
                            },
                            onEditClick = {
                                navController.navigate(AppScreens.EditPetScreen.route + "/${pet.id}")
                            },
                            onDeleteClick = {
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewListPetScreen() {
    val navController = rememberNavController()
    ListPetsScreen(navController)
}