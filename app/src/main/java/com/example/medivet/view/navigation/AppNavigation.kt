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
import com.example.medivet.utils.SessionManager
import com.example.medivet.view.screens.AuthenticationScreen
import com.example.medivet.view.screens.LoginScreen
import com.example.medivet.view.screens.PasswordResetScreen
import com.example.medivet.view.screens.UpdatePasswordScreen
import com.example.medivet.view.screens.chat.ChatScreen
import com.example.medivet.view.screens.clinics.ListClinicsScreen
import com.example.medivet.view.screens.dashboard.DashboardScreen
import com.example.medivet.view.screens.perfil.PerfilScreen
import com.example.medivet.view.screens.pets.CreatePetScreen
import com.example.medivet.view.screens.pets.EditPetScreen
import com.example.medivet.view.screens.pets.ListPetsScreen
import com.example.medivet.view.screens.pets.PetScreen
import com.example.medivet.view.screens.register.RegisterFirstScreen
import com.example.medivet.view.screens.register.RegisterSecondScreen
import com.example.medivet.viewModel.clinic.ClinicViewModel
import com.example.medivet.viewModel.clinic.ClinicViewModelFactory
import com.example.medivet.view.screens.clinics.ClinicScreen
import com.example.medivet.view.screens.clinics.ClinicAppointmentsMenuScreen
import com.example.medivet.view.screens.clinics.ScheduleAppointmentScreen
import com.example.medivet.viewModel.pet.PetsViewModel
import com.example.medivet.viewModel.pet.PetsViewModelFactory
import com.example.medivet.view.screens.clinics.MyAppointmentsScreen
@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val sessionManager = remember { SessionManager(context) }

    val clinicFactory = remember { ClinicViewModelFactory(sessionManager) }
    val clinicViewModel: ClinicViewModel = viewModel(factory = clinicFactory)

    val petsFactory = remember { PetsViewModelFactory(sessionManager) }
    val petsViewModel: PetsViewModel = viewModel(factory = petsFactory)

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
        composable(AppScreens.ListClinicsScreen.route){
            ListClinicsScreen(navController, clinicViewModel)
        }
        composable(
            route = AppScreens.ClinicScreen.route + "/{clinicId}",
            arguments = listOf(navArgument("clinicId") { type = NavType.IntType })
        ) { backStackEntry ->
            val clinicId = backStackEntry.arguments?.getInt("clinicId")
            ClinicScreen(navController, clinicId, clinicViewModel)
        }
        composable(
            route = AppScreens.ClinicAppointmentsMenuScreen.route + "/{clinicId}/{serviceId}",
            arguments = listOf(
                navArgument("clinicId") { type = NavType.IntType },
                navArgument("serviceId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val clinicId = backStackEntry.arguments?.getInt("clinicId")
            val serviceId = backStackEntry.arguments?.getInt("serviceId")
            ClinicAppointmentsMenuScreen(navController, clinicId, serviceId)
        }
        composable(
            route = AppScreens.ScheduleAppointmentScreen.route + "/{clinicId}",
            arguments = listOf(
                navArgument("clinicId") { type = NavType.IntType },
            )
        ) { backStackEntry ->
            val clinicId = backStackEntry.arguments?.getInt("clinicId")

            ScheduleAppointmentScreen(
                navController = navController,
                clinicId = clinicId,
                clinicViewModel = clinicViewModel,
                petsViewModel = petsViewModel
            )
        }
        composable(AppScreens.MyAppointmentsScreen.route) {
            MyAppointmentsScreen(navController, clinicViewModel)
        }
    }
}