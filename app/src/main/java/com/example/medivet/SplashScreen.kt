package com.example.medivet

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.animation.core.tween
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medivet.R
import com.example.medivet.navigation.AppScreens
import com.example.medivet.MainViewModel
import com.example.medivet.MainViewModelFactory
import com.example.medivet.utils.SessionManager
import kotlinx.coroutines.delay
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.FastOutSlowInEasing

@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current

    val sessionManager = remember { SessionManager(context) }

    val factory = remember { MainViewModelFactory(sessionManager) }

    val viewModel: MainViewModel = viewModel(factory = factory)

    val isLoggedIn by viewModel.isUserLoggedIn.collectAsState()

    var startAnimation by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.7f,
        animationSpec = tween(durationMillis = 1500, easing = { OvershootInterpolator(2f).getInterpolation(it) }),
        label = "scaleAnim"
    )

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2000),
        label = "alphaAnim"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
    }

    LaunchedEffect(isLoggedIn) {
        delay(2000)

        if (isLoggedIn != null) {
            val destination = if (isLoggedIn == true) {
                AppScreens.MainScreen.route
            } else {
                AppScreens.LoginScreen.route
            }

            navController.navigate(destination) {
                popUpTo(0) { inclusive = true }
            }
        }
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
                .scale(scale)
                .alpha(alpha)
        )
        Text(
            text = "Bienvenid@s",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.alpha(alpha)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    Splash(scale = 1f, alpha = 1f)
}