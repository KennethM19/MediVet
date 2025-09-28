package com.example.medivet.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.medivet.R
import com.example.medivet.navigation.AppScreens
import com.example.medivet.utils.getFirebaseErrorMessage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

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
            verticalArrangement = Arrangement.Center
        ) {
            LogoSection()
            Spacer(modifier = Modifier.height(32.dp))

            EmailInput(email) { email = it }
            Spacer(modifier = Modifier.height(16.dp))
            PasswordInput(password) { password = it }
            Spacer(modifier = Modifier.height(8.dp))

            ForgotPasswordText(context)
            Spacer(modifier = Modifier.height(24.dp))

            LoginButton(email, password, auth, navController, context, isLoading) { isLoading = it }
            Spacer(modifier = Modifier.height(12.dp))
            GoogleLoginButton { launcher.launch(googleSignInClient.signInIntent) }
            Spacer(modifier = Modifier.height(16.dp))

            RegisterText(context)
        }
    }
}

@Composable
fun LogoSection() {
    Image(
        painter = painterResource(id = R.drawable.logo_titulo),
        contentDescription = "App Logo",
        modifier = Modifier.size(250.dp)
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
fun ForgotPasswordText(context: Context) {
    Text(
        "¿Olvidaste tu contraseña?",
        fontSize = 14.sp,
        color = Color.Black,
        modifier = Modifier
            .clickable {
                Toast.makeText(context, "Funcionalidad en construcción", Toast.LENGTH_SHORT).show()
            }
    )
}

@Composable
fun LoginButton(
    email: String,
    password: String,
    auth: FirebaseAuth,
    navController: NavHostController,
    context: Context,
    isLoading: Boolean,
    setLoading: (Boolean) -> Unit
) {
    Button(
        onClick = {
            setLoading(true)
            auth.signInWithEmailAndPassword(email.trim(), password.trim())
                .addOnCompleteListener { task ->
                    setLoading(false)
                    if (task.isSuccessful) {
                        navController.navigate(AppScreens.MainScreen.route) {
                            popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            getFirebaseErrorMessage(task.exception),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("Login", "Error", task.exception)
                    }
                }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = !isLoading
    ) {
        Text("Iniciar sesión", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
fun RegisterText(context: Context) {
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
                Toast.makeText(context, "Registro en construcción", Toast.LENGTH_SHORT).show()
            }
        )
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


