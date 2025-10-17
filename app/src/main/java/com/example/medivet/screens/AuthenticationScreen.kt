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
) {
    val context = LocalContext.current
    val codeLength = 5
    var code by remember { mutableStateOf(List(codeLength) { "" }) }

    val sessionManager = remember { SessionManager(context) }

    val factory = remember { RegisterViewModelFactory(sessionManager) }

    val backStackEntry = navController.getBackStackEntry(AppScreens.RegisterFirstScreen.route)
    val viewModel: RegisterViewModel = viewModel(
        viewModelStoreOwner = backStackEntry,
        factory = factory
    )

    val state by viewModel.registerState.collectAsState()
    val regData by viewModel.registrationData.collectAsState()

    val emailText = regData.email.ifBlank { "el correo de registro" }
    val isLoading = state is RegisterState.Loading

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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

            AuthInstructionText(emailText)

            Spacer(modifier = Modifier.height(16.dp))

            AuthCodeInputFields(
                code=code,
                codeLength=codeLength,
                onCodeChange = { index, value ->
                code = code.toMutableList().also { it[index] = value }
            }
            )
            Spacer(modifier = Modifier.height(24.dp))

            SendAuthenticationButton(
                viewModel = viewModel,
                authcode = code.joinToString(""),
                isLoading = isLoading
            )

            RegisterAuthHandler(state, navController, context, viewModel)
        }
    }
}

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

@Composable
fun AuthCodeInputFields(code: List<String>, codeLength: Int,onCodeChange: (Int, String) -> Unit) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current


    Box {
        BasicTextField(
            value = code.joinToString(""),
            onValueChange = { input ->
                val chars = input.filter { it.isLetterOrDigit() }.take(codeLength)
                chars.forEachIndexed { i, c -> onCodeChange(i, c.toString()) }
                for (i in chars.length until codeLength) onCodeChange(i, "")

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

@Composable
fun SendAuthenticationButton(
    viewModel: RegisterViewModel,
    authcode: String,
    isLoading: Boolean
) {
    Button(
        onClick = {
            viewModel.verifyCode(authcode)
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

}