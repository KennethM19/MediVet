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

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreens.SplashScreen.route
    ) {
        composable(AppScreens.SplashScreen.route) {
            SplashScreen(navController)
        }
        composable(AppScreens.LoginScreen.route) {
            LoginScreen(navController)
        }
        composable(AppScreens.MainScreen.route) {
            MainScreen(navController)
        }
        composable(AppScreens.ResetPasswordScreen.route) {
            PasswordResetScreen(navController)
        }
        composable(AppScreens.UpdatePasswordScreen.route) {
            UpdatePasswordScreen(navController)
        }
    }
}