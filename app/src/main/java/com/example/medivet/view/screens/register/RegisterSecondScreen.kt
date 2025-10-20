package com.example.medivet.view.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.medivet.R
import com.example.medivet.view.navigation.AppScreens

@Composable
fun RegisterSecondScreen(navController: NavHostController) {
    LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    Box(modifier = Modifier.fillMaxWidth()) {
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
                Row {
                    InputFieldWithSubtitle(
                        subtitle = "Correo electrónico",
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    PasswordFieldWithSubtitle(
                        subtitle = "Contraseña",
                        value = password,
                        onValueChange = { password = it }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    PasswordFieldWithSubtitle(
                        subtitle = "Confirmar Contraseña",
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it }
                    )
                }
            }
            RegisterButton(navController)
        }
    }
}

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

@Composable
fun RegisterButton(navController: NavHostController) {
    Button(
        onClick = {
            navController.navigate(AppScreens.AuthenticationScreen.route)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
    ) {
        Text("Registrarse")
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewRegisterSecondScreen() {
    val navController = rememberNavController()
    RegisterSecondScreen(navController)
}