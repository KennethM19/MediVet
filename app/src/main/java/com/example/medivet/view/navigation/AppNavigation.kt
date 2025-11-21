package com.example.medivet.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.medivet.MainScreen
import com.example.medivet.SplashScreen
import com.example.medivet.model.repository.ChatRepository
import com.example.medivet.utils.SessionManager
import com.example.medivet.view.screens.AuthenticationScreen
import com.example.medivet.view.screens.LoginScreen
import com.example.medivet.view.screens.PasswordResetScreen
import com.example.medivet.view.screens.UpdatePasswordScreen
import com.example.medivet.view.screens.chat.ChatScreen
import com.example.medivet.view.screens.dashboard.DashboardScreen
import com.example.medivet.view.screens.perfil.PerfilScreen
import com.example.medivet.view.screens.pets.CreatePetScreen
import com.example.medivet.view.screens.pets.EditPetScreen
import com.example.medivet.view.screens.pets.ListPetsScreen
import com.example.medivet.view.screens.pets.PetScreen
import com.example.medivet.view.screens.register.RegisterFirstScreen
import com.example.medivet.view.screens.register.RegisterSecondScreen
import com.example.medivet.viewModel.chat.ChatViewModel
import com.example.medivet.viewModel.chat.ChatViewModelFactory
import okhttp3.OkHttpClient


@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val sessionManager = remember { SessionManager(context) }

    NavHost(
        navController = navController,
        startDestination = AppScreens.LoginScreen.route
    ) {
        composable(AppScreens.SplashScreen.route) {
            SplashScreen(navController)
        }
        composable(AppScreens.MainScreen.route) {
            MainScreen(navController = navController, sessionManager = sessionManager)
        }
        composable(AppScreens.ListPetsScreen.route) {
            ListPetsScreen(navController)
        }
        composable(AppScreens.CreatePetScreen.route) {
            CreatePetScreen(navController)
        }
        composable(
            route = AppScreens.EditPetScreen.route + "/{pet_Id}",
            arguments = listOf(navArgument("pet_Id") { type = NavType.IntType })
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getInt("pet_Id")
            EditPetScreen(navController, petId)
        }
        composable(AppScreens.LoginScreen.route) {
            LoginScreen(navController)
        }
        composable(AppScreens.PasswordResetScreen.route) {
            PasswordResetScreen(navController)
        }
        composable(
            route = AppScreens.PetScreen.route + "/{pet_Id}",
            arguments = listOf(navArgument("pet_Id") { type = NavType.IntType })
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getInt("pet_Id")
            PetScreen(navController, petId)
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
        composable(AppScreens.ProfileScreen.route) {
            PerfilScreen(navController)
        }
        composable(AppScreens.ChatScreen.route) {
            ChatScreen(navController)
        }
        composable(AppScreens.DashboardScreen.route) {
            DashboardScreen(navController)
        }

    }
}