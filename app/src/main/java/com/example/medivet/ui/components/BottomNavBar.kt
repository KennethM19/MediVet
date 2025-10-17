package com.example.medivet.ui.components

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.medivet.navigation.AppScreens

@Composable
fun BottomNavBar(navController: NavHostController, modifier: Modifier = Modifier) {
    NavigationBar(
        containerColor = Color(0xFF00BFA5),
        modifier = modifier
    ) {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(AppScreens.MainScreen.route) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(AppScreens.PetsScreen.route) },
            icon = { Icon(Icons.Default.Pets, contentDescription = "Mascotas") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* Navegar veterinarias */ },
            icon = { Icon(Icons.Default.LocalHospital, contentDescription = "Veterinarias") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* Navegar consulta */ },
            icon = { Icon(Icons.Default.Chat, contentDescription = "Consulta") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* Navegar información */ },
            icon = { Icon(Icons.Default.Info, contentDescription = "Información") }
        )
    }
}