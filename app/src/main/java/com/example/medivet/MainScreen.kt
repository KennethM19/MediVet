package com.example.medivet

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.medivet.model.model.ClinicResponse
import com.example.medivet.utils.LocationManager
import com.example.medivet.utils.SessionManager
import com.example.medivet.view.navigation.AppScreens
import com.example.medivet.viewModel.clinic.ClosestClinicViewModel
import com.example.medivet.viewModel.clinic.ClosestClinicViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    sessionManager: SessionManager
) {
    Log.d("CLINIC_DEBUG", "MainScreen COMENZÓ A DIBUJARSE")
    val context = LocalContext.current

    var token by remember { mutableStateOf<String?>(null) }

    // 1. Obtener token
    LaunchedEffect(Unit) {
        token = sessionManager.getToken()
        Log.d("CLINIC_DEBUG", "TOKEN OBTENIDO: $token")
    }

    // 2. Crear viewModel solo si el token ya existe
    val closestClinicViewModel: ClosestClinicViewModel? =
        if (token != null) {
            Log.d("CLINIC_DEBUG", "CREANDO ViewModel con token: $token")
            viewModel(factory = ClosestClinicViewModelFactory(context, token))
        } else null

    // 3. Crear launcher para solicitar permisos de ubicación
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Log.d("CLINIC_DEBUG", "PERMISO UBICACIÓN OTORGADO → loadClosestClinic()")
            closestClinicViewModel?.loadClosestClinic()
        } else {
            Log.d("CLINIC_DEBUG", "PERMISO UBICACIÓN DENEGADO")
        }
    }

    // 4. Cuando el ViewModel esté listo, verificar permisos
    LaunchedEffect(closestClinicViewModel) {
        if (closestClinicViewModel != null) {
            Log.d("CLINIC_DEBUG", "ViewModel LISTO → verificando permisos")

            val locationManager = LocationManager(context)

            if (!locationManager.hasPermission()) {
                Log.d("CLINIC_DEBUG", "NO HAY PERMISO → SOLICITANDO...")
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                Log.d("CLINIC_DEBUG", "PERMISO YA OTORGADO → Ejecutando loadClosestClinic()")
                closestClinicViewModel.loadClosestClinic()
            }
        }
    }

    // 5. Observar clínica
    val closestClinic by (closestClinicViewModel?.closestClinic?.collectAsState()
        ?: remember { mutableStateOf<ClinicResponse?>(null) })

    LaunchedEffect(closestClinic) {
        Log.d("CLINIC_DEBUG", "closestClinic cambió: $closestClinic")
    }

    // 6. User ViewModel
    val factory = remember { MainViewModelFactory(sessionManager, context) }
    val viewModel: MainViewModel = viewModel(factory = factory)

    val user by viewModel.user.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshUser()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("MediVet") }
            )
        }
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize()) {

            // 🔵 Fondo de pantalla (atrás de todo)
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = "Fondo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 🔵 Contenido encima del fondo
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                // ------------------ CARD DE PERFIL ------------------
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .clickable {
                                    navController.navigate("profile_screen")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (!user?.photo.isNullOrEmpty()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(user?.photo)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.LightGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Sin foto",
                                        modifier = Modifier.size(40.dp),
                                        tint = Color.Gray
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = user?.name ?: "[Nombre]",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = user?.email ?: "example@example.com",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ------------------ MENU PRINCIPAL ------------------
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column {
                        MenuItem("Mascotas", Icons.Default.Pets) {
                            navController.navigate("list_pets")
                        }
                        MenuItem("Veterinarias", Icons.Default.LocalHospital) {
                            navController.navigate("list_clinics")
                        }
                        MenuItem("Consulta", Icons.Default.Chat) {
                            navController.navigate("chat_screen")
                        }
                        MenuItem("Perfil", Icons.Default.Person) {
                            navController.navigate("profile_screen")
                        }
                        MenuItem("Dashboard", Icons.Default.Info) {
                            navController.navigate("dashboard_screen")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ------------------ RECORDATORIOS ------------------
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
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

                // ------------------ CLÍNICA MÁS CERCANA ------------------
                if (closestClinic != null) {
                    Box(
                        modifier = Modifier
                            .clickable {
                                navController.navigate(
                                    AppScreens.ClinicScreen.route + "/${closestClinic!!.id}"
                                )
                            }
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(6.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Clínica más cercana", fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(8.dp))
                                Text(closestClinic!!.name)
                                Text(closestClinic!!.address)
                            }
                        }
                    }
                } else {
                    Text(
                        "Buscando la clínica más cercana...",
                        color = Color.Gray,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ------------------ CERRAR SESIÓN ------------------
                Button(
                    onClick = {
                        viewModel.signOut()
                        navController.navigate(AppScreens.LoginScreen.route) {
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
fun MenuItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
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
