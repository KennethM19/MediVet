package com.example.medivet.screens.register

import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.medivet.navigation.AppScreens
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medivet.ui.register.RegisterViewModel
import com.example.medivet.ui.register.RegisterState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import com.example.medivet.ui.register.RegisterViewModelFactory
import com.example.medivet.utils.SessionManager
@Composable
fun RegisterSecondScreen(
    navController: NavHostController,
) {
    val context = LocalContext.current

    // 1. Crear la instancia UNICA del SessionManager
    val sessionManager = remember { SessionManager(context) }

    // 2. Crear la Factory, pasándole el SessionManager
    val factory = remember { RegisterViewModelFactory(sessionManager) }

    // 3. Obtener el ViewModel usando la Factory (ESTE ES EL CAMBIO CLAVE)
    val navBackStackEntry = remember { navController.getBackStackEntry(AppScreens.RegisterFirstScreen.route) }
    val viewModel: RegisterViewModel = viewModel(
        factory = factory,
        viewModelStoreOwner = navBackStackEntry
    )


    // Variables de estado locales
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // 4. Observar el estado de registro
    val state by viewModel.registerState.collectAsState()
    val isLoading = state is RegisterState.Loading


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
            verticalArrangement = Arrangement.SpaceBetween) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RegisterLogo()
                Spacer(modifier = Modifier.height(8.dp))
                // Correo electrónico
                Row() {
                    InputFieldWithSubtitle(
                        subtitle = "Correo electrónico",
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row() {
                    PasswordFieldWithSubtitle(
                        subtitle = "Contraseña",
                        value = password,
                        onValueChange = { password = it}
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row() {
                    PasswordFieldWithSubtitle(
                        subtitle = "Confirmar Contraseña",
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it}
                    )
                }
            }
            RegisterButton(
                viewModel = viewModel,
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                isLoading = isLoading
            )
        }
        RegisterAuthHandler(state, navController, context, viewModel)
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

//5. FUNCIÓN MODIFICADA DEL BOTÓN REGISTRARSE
@Composable
fun RegisterButton(
    viewModel: RegisterViewModel,
    email: String,
    password: String,
    confirmPassword: String,
    isLoading: Boolean
) {
    val context = LocalContext.current

    Button(
        onClick = {
            // 1. VALIDACIÓN DE COINCIDENCIA DE CONTRASEÑAS
            if (password != confirmPassword) {
                Toast.makeText(context, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                return@Button
            }

            // 2. 🚨 GUARDAR EMAIL Y PASSWORD EN EL VIEWMODEL PRIMERO 🚨
            // Usamos los setters para actualizar el estado interno del VM
            viewModel.setEmail(email.trim())
            viewModel.setPassword(password.trim())

            // 3. VALIDACIÓN FINAL EN EL VM
            // Llamar a la función de registro (que ahora lee el email/password directamente de su estado interno)
            viewModel.registerUser()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(Modifier.size(24.dp))
        } else {
            Text("Registrarse")
        }
    }
}

// 👈 6. FUNCIÓN MANEJADORA DE ESTADO
@Composable
fun RegisterAuthHandler(
    state: RegisterState,
    navController: NavHostController,
    context: Context,
    viewModel: RegisterViewModel
) {
    LaunchedEffect(state) {
        when (state) {
            is RegisterState.Success -> {
                Toast.makeText(context, "Registro exitoso. Verifique su correo.", Toast.LENGTH_LONG).show()
                // Navegar a la pantalla de Autenticación de código
                navController.navigate(AppScreens.AuthenticationScreen.route)
                viewModel.resetState() // Limpiar el estado después de navegar
            }
            is RegisterState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> Unit
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewRegisterSecondScreen() {
    val navController = rememberNavController()
    RegisterSecondScreen(navController)
}