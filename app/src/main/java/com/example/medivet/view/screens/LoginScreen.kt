package com.example.medivet.view.screens

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.medivet.R
import com.example.medivet.utils.SessionManager
import com.example.medivet.view.navigation.AppScreens
import com.example.medivet.viewModel.login.AuthState
import com.example.medivet.viewModel.login.LoginViewModel
import com.example.medivet.viewModel.login.LoginViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    val sessionManager = remember { SessionManager(context) }

    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(sessionManager)

    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()

    val token = context.getString(R.string.default_web_client_id)
    val googleSignInClient = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(token)
            .requestEmail()
            .build()
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleGoogleLoginResult(result, auth, navController, context)
    }

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
            LogoSection()
            Spacer(modifier = Modifier.height(32.dp))

            EmailInput(email) { email = it }
            Spacer(modifier = Modifier.height(16.dp))
            PasswordInput(password) { password = it }
            Spacer(modifier = Modifier.height(8.dp))

            ForgotPasswordText(navController, context)
            Spacer(modifier = Modifier.height(24.dp))

            val isLoading = authState is AuthState.Loading

            LoginButton(email, password, viewModel, isLoading)
            Spacer(modifier = Modifier.height(12.dp))
            GoogleLoginButton { launcher.launch(googleSignInClient.signInIntent) }
            Spacer(modifier = Modifier.height(16.dp))

            RegisterText(navController, context)

            AuthHandler(authState = authState, navController = navController, context = context)
        }
    }
}

@Composable
fun LogoSection() {
    Image(
        painter = painterResource(id = R.drawable.logo_titulo),
        contentDescription = "Logo",
        modifier = Modifier
            .size(250.dp)
            .offset(y = 30.dp)
    )
}

@Composable
fun EmailInput(value: String, onValueChange: (String) -> Unit) {
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

@Composable
fun PasswordInput(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Contraseña") },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        textStyle = LocalTextStyle.current.copy(color = Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.85f), shape = MaterialTheme.shapes.small)
    )
}

@Composable
fun ForgotPasswordText(navController: NavHostController, context: Context) {
    Text(
        "¿Olvidaste tu contraseña?",
        fontSize = 14.sp,
        color = Color.Black,
        modifier = Modifier
            .clickable {
                navController.navigate(AppScreens.PasswordResetScreen.route)
            }
    )
}

@Composable
fun LoginButton(
    email: String,
    password: String,
    viewModel: LoginViewModel,
    isLoading: Boolean
) {
    Button(
        onClick = {
            viewModel.signIn(email, password)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
        } else {
            Text("Iniciar sesión", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun GoogleLoginButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = "Google icon",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Sign in with Google", color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RegisterText(navController: NavHostController, context: Context) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("¿No tienes cuenta?")
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            "Regístrate",
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.clickable {
                navController.navigate(AppScreens.RegisterFirstScreen.route)
            }
        )
    }
}

@Composable
fun AuthHandler(
    authState: AuthState,
    navController: NavHostController,
    context: Context
) {
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                Toast.makeText(
                    context,
                    "Login con ${authState.method} exitoso.",
                    Toast.LENGTH_SHORT
                ).show()
                navController.navigate(AppScreens.MainScreen.route) {
                    popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                }
            }

            is AuthState.Error -> {
                Toast.makeText(context, authState.message, Toast.LENGTH_LONG).show()
            }

            else -> Unit
        }
    }
}

private fun handleGoogleLoginResult(
    result: ActivityResult,
    auth: FirebaseAuth,
    navController: NavHostController,
    context: Context
) {
    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
    try {
        val account = task.getResult(ApiException::class.java)
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener { authResult ->
            if (authResult.isSuccessful) {
                Toast.makeText(context, "Login con Google exitoso", Toast.LENGTH_SHORT).show()
                navController.navigate(AppScreens.MainScreen.route) {
                    popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                }
            } else {
                Toast.makeText(context, "Error en login con Google", Toast.LENGTH_SHORT).show()
            }
        }
    } catch (e: ApiException) {
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}