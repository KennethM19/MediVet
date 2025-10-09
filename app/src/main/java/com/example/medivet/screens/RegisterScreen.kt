package com.example.medivet.screens

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.medivet.R
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.medivet.navigation.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavHostController) {
    val context = LocalContext.current
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            item { RegisterLogo() }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            val fields = listOf<@Composable () -> Unit>(
                { InputFieldWithSubtitle("Nombre", firstName) { firstName = it } },
                { Spacer(modifier = Modifier.height(12.dp)) },
                { InputFieldWithSubtitle("Apellidos", lastName) { lastName = it } },
                { Spacer(modifier = Modifier.height(12.dp)) },
                { InputFieldWithSubtitle("Número de teléfono", phoneNumber, KeyboardType.Phone) { phoneNumber = it } },
                { Spacer(modifier = Modifier.height(12.dp)) },
                { InputFieldWithSubtitle("Correo electrónico", email, KeyboardType.Email) { email = it } },
                { Spacer(modifier = Modifier.height(12.dp)) },
                { PasswordFieldWithSubtitle("Contraseña", password) { password = it } },
                { Spacer(modifier = Modifier.height(12.dp)) },
                { PasswordFieldWithSubtitle("Confirmar contraseña", confirmPassword) { confirmPassword = it } },
                { Spacer(modifier = Modifier.height(24.dp)) },
                { RegisterButton(navController) },
                { Spacer(modifier = Modifier.height(48.dp)) }
            )

            items(fields) { it() }
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
            .size(250.dp)
            .offset(y = 30.dp)
    )
}

// Input normal con subtítulo arriba
@Composable
fun InputFieldWithSubtitle(
    subtitle: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
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

// Input de contraseña con subtítulo
@Composable
fun PasswordFieldWithSubtitle(
    subtitle: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = subtitle, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(subtitle) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.85f), shape = MaterialTheme.shapes.small)
        )
    }
}

// Botón registrar
@Composable
fun RegisterButton(navController: NavHostController) {
    Button(
        onClick = {
        /* Funcionalidad se agregará después */
            navController.navigate(AppScreens.AuthenticationScreen.route)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text("Registrarte")
    }
}
