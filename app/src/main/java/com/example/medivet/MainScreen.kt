package com.example.medivet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medivet.R
import com.example.medivet.navigation.AppScreens
import com.example.medivet.MainViewModel //ViewModel
import com.example.medivet.MainViewModelFactory //Factory
import com.example.medivet.utils.SessionManager //SessionManager (DataStore)
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    val context = LocalContext.current

    // 1. Crear la instancia UNICA del SessionManager
    val sessionManager = remember { SessionManager(context) }

    // 2. Crear la Factory, pasándole el SessionManager
    val factory = remember { MainViewModelFactory(sessionManager) }

    // 3. OBTENER EL VIEWMODEL USANDO LA FACTORY (ARREGLO DEL CRASH)
    val viewModel: MainViewModel = viewModel(factory = factory)

    // Los datos del usuario (que el VM carga)
    val user by viewModel.user.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("MediVet") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Fondo
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = "Fondo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // 🔹 Tarjeta de perfil
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Imagen circular vacía (por ahora)
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            // Muestra el nombre cargado del usuario
                            text = user?.name ?: "[Nombre]",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            // Muestra el email cargado
                            text = user?.email ?: "example@example.com",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 🔹 Opciones del menú principal
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column {
                        MenuItem("Mascotas", Icons.Default.Pets) {
                            navController.navigate("pets_screen")
                        }
                        MenuItem("Veterinarias", Icons.Default.LocalHospital) { /* Navegar */ }
                        MenuItem("Consulta", Icons.Default.Chat) { /* Navegar */ }
                        MenuItem("Perfil", Icons.Default.Person) { /* Navegar */ }
                        MenuItem("Información", Icons.Default.Info) { /* Navegar */ }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 🔹 Tarjeta de recordatorios
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Próximo recordatorio", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Event, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sin recordatorios")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 🔹 Botón de Sign Out
                Button(
                    onClick = {
                        // Llama a la función que borra el token de DataStore y Firebase
                        viewModel.signOut()
                        navController.navigate(AppScreens.LoginScreen.route) {
                            // Limpia el back stack para que el usuario no pueda volver
                            popUpTo(AppScreens.MainScreen.route) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar sesión", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun MenuItem(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ArrowForwardIos, contentDescription = null)
    }
}