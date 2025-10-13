package com.example.medivet.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.medivet.R
import com.example.medivet.navigation.AppScreens


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterFirstScreen(navController: NavHostController) {
    LocalContext.current
    var docType by remember { mutableStateOf("") }
    var docNumber by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var cellphoneNum by remember { mutableStateOf("") }
    var telephoneNum by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RegisterLogo()
                Spacer(modifier = Modifier.height(8.dp))
                // FILA DE DOCUMENTO
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DocumentTypeDropdown(
                        selectedType = docType,
                        onTypeSelected = {docType = it},
                        modifier = Modifier.weight(1f)
                    )
                    InputFieldWithSubtitle(
                        subtitle = "N° documento",
                        value = docNumber,
                        onValueChange = { docNumber = it },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                // FILA DE NOMBRE Y APELLIDO
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InputFieldWithSubtitle(
                        subtitle = "Nombre",
                        value = firstName,
                        onValueChange = { firstName = it },
                        modifier = Modifier.weight(1f)
                    )
                    InputFieldWithSubtitle(
                        subtitle = "Apellido",
                        value = lastName,
                        onValueChange = { lastName = it },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                //FILA DE NACIMIENTO Y DIRECCIÓN
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InputFieldWithSubtitle(
                        subtitle = "Nacimiento",
                        value = birthDate,
                        onValueChange = { birthDate = it },
                        modifier = Modifier.weight(1f)
                    )
                    InputFieldWithSubtitle(
                        subtitle = "Dirección",
                        value = address,
                        onValueChange = { address = it },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                //FILA DE NUMERO DE TELEFONO O CELULAR
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InputFieldWithSubtitle(
                        subtitle = "Celular",
                        value = cellphoneNum,
                        onValueChange = { cellphoneNum = it },
                        modifier = Modifier.weight(1f)
                    )
                    InputFieldWithSubtitle(
                        subtitle = "Teléfono",
                        value = telephoneNum,
                        onValueChange = { telephoneNum = it },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            //BOTÓN PARA CONTINUAR EL REGISTRO
            ContinueButton(navController)
        }
    }
}

// Logo
@Composable
fun RegisterLogo() {
    Image(
        painter = painterResource(id = R.drawable.logo_titulo),
        contentDescription = "Logo",
        modifier = Modifier
            .size(200.dp)
            .offset(y = 30.dp)
    )
}

// Input normal con subtítulo arriba
@Composable
fun InputFieldWithSubtitle(
    subtitle: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        Text(text = subtitle, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(subtitle) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.85f), shape = MaterialTheme.shapes.small)
        )
    }
}

// Botón Continuar con el registro
@Composable
fun ContinueButton(navController: NavHostController) {
    Button(
        onClick = {
            /* Funcionalidad se agregará después */
            navController.navigate(AppScreens.RegisterSecondScreen.route)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
    ) {
        Text("Continuar")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentTypeDropdown(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val types = listOf("DNI", "Pasaporte")

    Column(modifier = modifier) {
        Text(text = "Tipo de documento", color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded}
        ) {
            OutlinedTextField(
                value = selectedType,
                onValueChange = {},
                label = {Text("")},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)},
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.85f), shape = MaterialTheme.shapes.small)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false}
            ) {
                types.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type)},
                        onClick = {
                            onTypeSelected(type)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewRegisterFirstScreen() {
    val navController = rememberNavController()
    RegisterFirstScreen(navController)
}

