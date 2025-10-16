package com.example.medivet.ui.pets

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.medivet.model.PetRequest
import com.example.medivet.utils.SessionManager
import kotlinx.coroutines.launch
import com.example.medivet.R

@Composable
fun CreatePetScreen(
    navController: NavHostController,
    viewModel: CreatePetViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = CreatePetViewModelFactory(SessionManager(LocalContext.current))
    )
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val scope = rememberCoroutineScope()

    val sexOptions by viewModel.sexOptions.collectAsState()
    val speciesOptions by viewModel.speciesOptions.collectAsState()
    val breedOptions by viewModel.breedOptions.collectAsState()
    val creationState by viewModel.creationState.collectAsState()

    var name by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var numDoc by remember { mutableStateOf("") }
    var isNeutered by remember { mutableStateOf(false) }

    var selectedSex by remember { mutableStateOf<Int?>(null) }
    var selectedSpecies by remember { mutableStateOf<Int?>(null) }
    var selectedBreed by remember { mutableStateOf<Int?>(null) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Lanzador para abrir la galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    LaunchedEffect(Unit) { viewModel.loadDropdownData() }

    Column(modifier = Modifier.fillMaxSize()) {
        // Encabezado con fondo
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
                text = "Agregar Mascota",
                style = MaterialTheme.typography.headlineSmall.copy(color = Color.Black),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        // Contenido principal
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

                    DropdownSelector(
                        label = "Especie*",
                        options = speciesOptions.map { it.name },
                        selectedIndex = speciesOptions.indexOfFirst { it.id == selectedSpecies },
                        onOptionSelected = { selectedSpecies = speciesOptions[it].id }
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre*", color = Color.Black) },
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))

                    DropdownSelector(
                        label = "Sexo*",
                        options = sexOptions.map { it.name },
                        selectedIndex = sexOptions.indexOfFirst { it.id == selectedSex },
                        onOptionSelected = { selectedSex = sexOptions[it].id }
                    )
                    Spacer(Modifier.height(12.dp))

                    DropdownSelector(
                        label = "Raza*",
                        options = breedOptions.map { it.name },
                        selectedIndex = breedOptions.indexOfFirst { it.id == selectedBreed },
                        onOptionSelected = { selectedBreed = breedOptions[it].id }
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Peso (kg)*", color = Color.Black) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("¿Está castrado?*", color = Color.Black, modifier = Modifier.weight(1f))
                        Switch(checked = isNeutered, onCheckedChange = { isNeutered = it })
                    }
                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = year,
                            onValueChange = { year = it },
                            label = { Text("Año nacimiento*", color = Color.Black) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = LocalTextStyle.current.copy(color = Color.Black),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = month,
                            onValueChange = { month = it },
                            label = { Text("Mes nacimiento*", color = Color.Black) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = LocalTextStyle.current.copy(color = Color.Black),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = numDoc,
                        onValueChange = { numDoc = it },
                        label = { Text("Número de documento (DNI)*", color = Color.Black) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(24.dp))

                    // Imagen / Subir foto
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color(0xFFF4F4F4), RoundedCornerShape(12.dp))
                            .clickable {
                                galleryLauncher.launch("image/*")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(selectedImageUri),
                                contentDescription = "Foto de la mascota",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Seleccionar foto",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text("Suba foto de su mascota", color = Color.Gray)
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar", color = Color.White)
                        }

                        Spacer(Modifier.width(16.dp))

                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFA5)),
                            onClick = {
                                scope.launch {
                                    val userId = sessionManager.getUserIdFromToken() ?: return@launch

                                    val pet = PetRequest(
                                        user_id = userId,
                                        num_doc = numDoc,
                                        name = name,
                                        photo = selectedImageUri?.toString(), // ✅ Guardamos la URI seleccionada
                                        sex_id = selectedSex ?: return@launch,
                                        specie_id = selectedSpecies ?: return@launch,
                                        year_birth = year.toIntOrNull() ?: return@launch,
                                        month_birth = month.toIntOrNull() ?: return@launch,
                                        weight = weight.toDoubleOrNull(),
                                        neutered = isNeutered,
                                        breed_id = selectedBreed ?: return@launch
                                    )
                                    viewModel.createPet(pet)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Agregar", color = Color.White)
                        }
                    }

                    if (creationState is PetCreationState.Loading)
                        CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}

/**
 * Dropdown estilizado con texto negro
 */
@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, color = Color.Black)
        Box {
            OutlinedTextField(
                value = options.getOrNull(selectedIndex) ?: "",
                onValueChange = {},
                readOnly = true,
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Black)
                    }
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = { Text(option, color = Color.Black) },
                        onClick = {
                            onOptionSelected(index)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
