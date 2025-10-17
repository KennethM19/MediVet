package com.example.medivet.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel // 👈 ViewModel
import com.example.medivet.R
import com.example.medivet.navigation.AppScreens
import com.example.medivet.ui.register.RegisterViewModel // 👈 VM de Registro
import com.example.medivet.ui.register.RegisterState // 👈 Estado
import com.example.medivet.ui.register.RegisterViewModelFactory
import com.example.medivet.utils.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationScreen(
    navController: NavHostController,
    // Eliminamos la inicialización directa aquí, y la haremos dentro del bloque:
) {
    val context = LocalContext.current
    val codeLength = 6
    var code by remember { mutableStateOf(List(codeLength) { "" }) }

    // 1. Crear la instancia UNICA del SessionManager
    val sessionManager = remember { SessionManager(context) }

    // 2. Crear la Factory, pasándole el SessionManager
    val factory = remember { RegisterViewModelFactory(sessionManager) }

    // 3. Obtener el ViewModel usando la Factory (REEMPLAZA LA LÍNEA ANTERIOR)
    val viewModel: RegisterViewModel = viewModel(factory = factory)

    // 4. OBSERVAR EL ESTADO Y LOS DATOS DE REGISTRO
    val state by viewModel.registerState.collectAsState()
    val regData by viewModel.registrationData.collectAsState()

    // Obtener el email del VM para la instrucción
    val emailText = regData.email.ifBlank { "el correo de registro" }
    val isLoading = state is RegisterState.Loading

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            AuthenticationLogo()
            Spacer(modifier = Modifier.height(60.dp))

            // 👈 3. USAR EL EMAIL REAL
            AuthInstructionText(emailText)

            Spacer(modifier = Modifier.height(16.dp))

            AuthCodeInputFields(code) { index, value ->
                code = code.toMutableList().also { it[index] = value }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 👈 4. BOTÓN CON LÓGICA DE VERIFICACIÓN
            SendAuthenticationButton(
                viewModel = viewModel,
                authcode = code.joinToString(""),
                isLoading = isLoading
            )

            // 👈 5. MANEJADOR DE ESTADO
            RegisterAuthHandler(state, navController, context)
        }
    }
}

// Logo (SIN CAMBIOS)
@Composable
fun AuthenticationLogo() {
    Image(
        painter = painterResource(id = R.drawable.logo_titulo),
        contentDescription = "Logo",
        modifier = Modifier
            .size(250.dp)
            .offset(y = 30.dp)
    )
}

// 👈 FUNCIÓN MODIFICADA: Ahora recibe el email para mostrarlo
@Composable
fun AuthInstructionText(email: String) {
    Text(
        "Se envió un código a $email",
        color = Color.Black,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

// Input de código en recuadros individuales (SIN CAMBIOS)
@Composable
fun AuthCodeInputFields(code: List<String>, onCodeChange: (Int, String) -> Unit) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box {
        // Campo invisible para capturar todo el input
        BasicTextField(
            value = code.joinToString(""),
            onValueChange = { input ->
                val digits = input.filter { it.isDigit() }.take(6)
                digits.forEachIndexed { i, c -> onCodeChange(i, c.toString()) }
                for (i in digits.length until 6) onCodeChange(i, "")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            ),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = Color.Transparent),
            cursorBrush = SolidColor(Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .focusRequester(focusRequester)
        )

        // Los 6 recuadros visibles
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            code.forEach { digit ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .background(
                            Color.White.copy(alpha = 0.85f),
                            shape = MaterialTheme.shapes.small
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = digit,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }
}

// 👈 FUNCIÓN MODIFICADA: Llama al ViewModel para verificar
@Composable
fun SendAuthenticationButton(
    viewModel: RegisterViewModel,
    authcode: String,
    isLoading: Boolean
) {
    Button(
        onClick = {
            // Llama a la lógica de verificación del ViewModel
            //viewModel.verifyCode(authcode)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
        } else {
            Text("Verificar")
        }
    }
}

// 👈 FUNCIÓN ADICIONAL: Manejador de estado de verificación (añadir al mismo archivo)
@Composable
fun RegisterAuthHandler(state: RegisterState, navController: NavHostController, context: Context) {
    val viewModel: RegisterViewModel = viewModel() // Acceso al VM para resetear

    LaunchedEffect(state) {
        when (state) {
            is RegisterState.Success -> {
                Toast.makeText(context, "Verificación exitosa. ¡Bienvenido!", Toast.LENGTH_LONG).show()
                navController.navigate(AppScreens.MainScreen.route) {
                    popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                }
                viewModel.resetState()
            }
            is RegisterState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> Unit
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewAuthenticationFirstScreen() {
    val navController = rememberNavController()
    // Nota: El Preview ya no funcionará completamente sin un ViewModel real.
    // AuthenticationScreen(navController = navController)
}