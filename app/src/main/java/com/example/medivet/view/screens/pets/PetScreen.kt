package com.example.medivet.view.screens.pets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.NoFood
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.medivet.R
import com.example.medivet.ui.components.ButtonUtilsPet
import com.example.medivet.ui.components.InfoPetRow
import com.example.medivet.utils.SessionManager
import com.example.medivet.viewModel.pet.PetsViewModel
import com.example.medivet.viewModel.pet.PetsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetScreen( navController: NavHostController, petId: Int?) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val factory = PetsViewModelFactory(sessionManager)
    val petViewModel: PetsViewModel = viewModel(factory = factory)

    val pet by petViewModel.pet.collectAsState()

    LaunchedEffect(petId) {
        petId?.let { petViewModel.loadPet(it) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Text(
                text = pet?.name ?: "Cargando...",
                style = MaterialTheme.typography.headlineSmall.copy(color = Color.Black),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        //IMAGEN DE LA MASCOTA
                        Image(
                            painter = if (pet?.photo.isNullOrBlank()) {
                                painterResource(id = R.drawable.logo_titulo)
                            } else {
                                rememberAsyncImagePainter(
                                    model = pet?.photo
                                )
                            },
                            contentDescription = pet?.name,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    InfoPetRow(label = "Nombre:", value = pet?.name ?: "-")
                    InfoPetRow(label = "Especie:", value = pet?.species?.name ?: "-")
                    InfoPetRow(label = "Raza:", value = pet?.breed?.name ?: "-")
                    InfoPetRow(label = "Sexo:", value = pet?.sex?.name ?: "-")
                    InfoPetRow(label = "Peso:", value = "${pet?.weight ?: "-"} kg")
                    InfoPetRow(
                        label = "Edad:",
                        value = "${pet?.age?.years ?: 0} años y ${pet?.age?.months ?: 0} meses"
                    )

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column (horizontalAlignment = Alignment.CenterHorizontally){
                            ButtonUtilsPet(
                                onClick = {/* Navegar a hisorial clínico*/},
                                icon = Icons.Default.ContentPaste,
                                text = "Historial clínico"
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            ButtonUtilsPet(
                                onClick = {/* Navegar a dieta*/},
                                icon = Icons.Default.NoFood,
                                text = "Dieta"
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            ButtonUtilsPet(
                                onClick = {/* Navegar a hisorial clínico*/},
                                icon = Icons.Default.Medication,
                                text = "Receta"
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            ButtonUtilsPet(
                                onClick = {/* Navegar a dieta*/},
                                icon = Icons.Default.Accessibility,
                                text = "Actividades"
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            ButtonUtilsPet(
                                onClick = {/* Navegar a dieta*/},
                                icon = Icons.Default.StackedLineChart,
                                text = "Estadísticas"
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewPetScreen() {
    val navController = rememberNavController()
    val petId = 1
    PetScreen(navController, petId)
}