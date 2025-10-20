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
import com.example.medivet.screens.register.RegisterFirstScreen
import com.example.medivet.screens.AuthenticationScreen
import com.example.medivet.screens.register.RegisterSecondScreen
import com.example.medivet.ui.pets.CreatePetScreen
import com.example.medivet.ui.pets.PetsScreen



@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreens.SplashScreen.route) {
        composable(AppScreens.SplashScreen.route) {
            SplashScreen(navController)
        }
        composable(AppScreens.MainScreen.route) {
            MainScreen(navController)
        }
        composable(AppScreens.PetsScreen.route) {
            PetsScreen(navController)
        }
        composable(AppScreens.CreatePetScreen.route) {
            CreatePetScreen(navController)
        }
        composable(AppScreens.LoginScreen.route) {
            LoginScreen(navController)
        }
        composable(AppScreens.PasswordResetScreen.route) {
            PasswordResetScreen(navController)
        }
        composable(AppScreens.UpdatePasswordScreen.route) {
            UpdatePasswordScreen(navController)
        }
        composable(AppScreens.RegisterFirstScreen.route) {
            RegisterFirstScreen(navController)
        }
        composable(AppScreens.RegisterSecondScreen.route) {
            RegisterSecondScreen(navController)
        }
        composable(AppScreens.AuthenticationScreen.route) {
            AuthenticationScreen(navController)
        }
    }
}