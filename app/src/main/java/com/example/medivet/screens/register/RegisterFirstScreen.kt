package com.example.medivet.screens.register

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medivet.R
import com.example.medivet.navigation.AppScreens
import com.example.medivet.ui.register.RegisterViewModel
import com.example.medivet.ui.register.RegisterViewModelFactory
import com.example.medivet.ui.register.RegistrationData
import com.example.medivet.utils.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterFirstScreen(
    navController: NavHostController,
) {
    val context = LocalContext.current

    // 1. Crear la instancia UNICA del SessionManager
    val sessionManager = remember { SessionManager(context) }

    // 2. Crear la Factory, pasándole el SessionManager
    val factory = remember { RegisterViewModelFactory(sessionManager) }

    // 3. Obtener el ViewModel usando la Factory
    val viewModel: RegisterViewModel = viewModel(factory = factory)

    // 4. Leemos el estado completo del formulario (EN VIVO desde el VM)
    val regData by viewModel.registrationData.collectAsState()

    // ⚠️ Se eliminaron todas las variables locales 'var x by remember'

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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DocumentTypeDropdown(
                        selectedType = regData.docType,
                        onTypeSelected = viewModel::setDocType,
                        modifier = Modifier.weight(1f)
                    )
                    InputFieldWithSubtitle(
                        subtitle = "N° documento",
                        value = regData.docNumber,
                        onValueChange = viewModel::setDocNumber,
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InputFieldWithSubtitle(
                        subtitle = "Nombre",
                        value = regData.firstName,
                        onValueChange = viewModel::setFirstName,
                        modifier = Modifier.weight(1f)
                    )
                    InputFieldWithSubtitle(
                        subtitle = "Apellido",
                        value = regData.lastName,
                        onValueChange = viewModel::setLastName,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InputFieldWithSubtitle(
                        subtitle = "Nacimiento",
                        value = regData.birthDate,
                        onValueChange = viewModel::setBirthDate,
                        modifier = Modifier.weight(1f)
                    )
                    InputFieldWithSubtitle(
                        subtitle = "Dirección",
                        value = regData.address,
                        onValueChange = viewModel::setAddress,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InputFieldWithSubtitle(
                        subtitle = "Celular",
                        value = regData.cellphoneNum,
                        onValueChange = viewModel::setCellphoneNum,
                        keyboardType = KeyboardType.Phone,
                        modifier = Modifier.weight(1f)
                    )
                    InputFieldWithSubtitle(
                        subtitle = "Teléfono",
                        value = regData.telephoneNum,
                        onValueChange = viewModel::setTelephoneNum,
                        keyboardType = KeyboardType.Phone,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            ContinueButton(
                navController = navController,
                regData = regData
            )
        }
    }
}

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

@Composable
fun ContinueButton(
    navController: NavHostController,
    regData: RegistrationData
) {
    val context = LocalContext.current
    Button(
        onClick = {
            if (regData.docType.isBlank() || regData.docNumber.isBlank() || regData.firstName.isBlank() ||
                regData.lastName.isBlank() || regData.address.isBlank() || regData.birthDate.isBlank()) {

                Toast.makeText(context, "Por favor, complete todos los campos obligatorios.", Toast.LENGTH_LONG).show()
                return@Button
            }

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

}