package com.example.medivet.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePasswordScreen(navController: NavHostController) {
    LocalContext.current
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
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
            verticalArrangement = Arrangement.Top
        ) {
            UpdatePasswordLogo()
            Spacer(modifier = Modifier.height(32.dp))
            InstructionTextUpdate()
            Spacer(modifier = Modifier.height(16.dp))
            PasswordInputField("Nueva contraseña", newPassword) { newPassword = it }
            Spacer(modifier = Modifier.height(16.dp))
            InstructionTextUpdate2()
            PasswordInputField("Confirmar contraseña", confirmPassword) { confirmPassword = it }
            Spacer(modifier = Modifier.height(24.dp))
            ConfirmButton()
        }
    }
}

@Composable
fun UpdatePasswordLogo() {
    Image(
        painter = painterResource(id = R.drawable.logo_titulo),
        contentDescription = "Logo",
        modifier = Modifier
            .size(250.dp)
            .offset(y = 30.dp)
    )
}

@Composable
fun InstructionTextUpdate() {
    Text(
        "Nueva contraseña",
        color = Color.Black
    )
}

@Composable
fun InstructionTextUpdate2() {
    Text(
        "Confirmar contraseña",
        color = Color.Black
    )
}

@Composable
fun PasswordInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        textStyle = LocalTextStyle.current.copy(color = Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.85f), shape = MaterialTheme.shapes.small)
    )
}

@Composable
fun ConfirmButton() {
    Button(
        onClick = { /* Funcionalidad se agregará después */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text("Cambiar")
    }
}
