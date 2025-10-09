package com.example.medivet.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medivet.MainScreen
import com.example.medivet.SplashScreen
import com.example.medivet.screens.LoginScreen
import com.example.medivet.screens.PasswordResetScreen
import com.example.medivet.screens.UpdatePasswordScreen
import com.example.medivet.screens.RegisterScreen
import com.example.medivet.screens.AuthenticationScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreens.LoginScreen.route) {
        composable(AppScreens.LoginScreen.route) {
            LoginScreen(navController)
        }
        composable(AppScreens.PasswordResetScreen.route) {
            PasswordResetScreen(navController)
        }
        composable(AppScreens.UpdatePasswordScreen.route) {
            UpdatePasswordScreen(navController)
        }
        composable(AppScreens.RegisterScreen.route) {
            RegisterScreen(navController)
        }
        composable(AppScreens.AuthenticationScreen.route) {
            AuthenticationScreen(navController)
        }
    }
}
