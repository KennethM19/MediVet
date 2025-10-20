package com.example.medivet

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.medivet.navigation.AppScreens
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(navController: NavHostController) {
    var startAnimation by remember { mutableStateOf(false) }

    // Animación de escala (zoom)
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.7f,
        animationSpec = tween(
            durationMillis = 2000,
            easing = { OvershootInterpolator(2f).getInterpolation(it) }
        ), label = "scaleAnim"
    )

    // Animación de opacidad (fade-in)
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2000),
        label = "alphaAnim"
    )

    // Lanzar animación y luego navegar
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(3000) // tiempo visible del splash
        navController.popBackStack() // evita volver al splash
        navController.navigate(AppScreens.LoginScreen.route) //direccionar al login
    }

    Splash(scale, alpha)
}

@Composable
fun Splash(scale: Float, alpha: Float) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_titulo),
            contentDescription = "Logo + Titulo",
            modifier = Modifier
                .size(300.dp)
                .scale(scale)   // animación de zoom
                .alpha(alpha)   // animación de fade-in
        )
        Text(
            text = "Bienvenid@s",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.alpha(alpha) // también aparece con fade-in
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    Splash(scale = 1f, alpha = 1f)
}