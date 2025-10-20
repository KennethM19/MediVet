package com.example.medivet.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.medivet.view.navigation.AppScreens

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