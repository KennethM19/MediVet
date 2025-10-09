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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.medivet.R
import com.example.medivet.navigation.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordResetScreen(navController: NavHostController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Contenido encima del fondo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            PasswordResetLogo()
            Spacer(modifier = Modifier.height(60.dp))
            InstructionText()
            Spacer(modifier = Modifier.height(16.dp))
            PasswordResetEmailInput(email) { email = it }
            Spacer(modifier = Modifier.height(24.dp))
            SendResetButton(navController, email)
        }
    }
}

// Logo
@Composable
fun PasswordResetLogo() {
    Image(
        painter = painterResource(id = R.drawable.logo_titulo),
        contentDescription = "Logo",
        modifier = Modifier
            .size(250.dp)
            .offset(y = 30.dp)
    )
}

// Texto de instrucción
@Composable
fun InstructionText() {
    Text(
        "Ingrese su correo",
        color = Color.Black,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

// Input de correo
@Composable
fun PasswordResetEmailInput(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Correo electrónico") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        textStyle = LocalTextStyle.current.copy(color = Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.85f), shape = MaterialTheme.shapes.small)
    )
}

// Botón de enviar enlace
@Composable
fun SendResetButton(navController: NavHostController, email: String) {
    Button(
        onClick = { navController.navigate(AppScreens.UpdatePasswordScreen.route) },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text("Verificar")
    }
}
