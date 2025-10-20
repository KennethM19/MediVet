package com.example.medivet.view.navigation

sealed class AppScreens(val route: String) {
    object SplashScreen : AppScreens("splash_screen")
    object MainScreen : AppScreens("main_screen")

    object LoginScreen : AppScreens("login_screen")
    object RegisterFirstScreen : AppScreens("register_first_screen")
    object RegisterSecondScreen : AppScreens("register_second_Screen")
    object PasswordResetScreen : AppScreens("password_reset_screen")
    object UpdatePasswordScreen : AppScreens("update_password_screen")
    object AuthenticationScreen : AppScreens("authentication_screen")

    object PetsScreen : AppScreens("pets_screen")
    object CreatePetScreen : AppScreens("create_pet_screen")
    object EditPetScreen : AppScreens("edit_pet_screen")
    object ProfileScreen : AppScreens("profile_screen")
    object InfoScreen : AppScreens("info_screen")
    object ConsultScreen : AppScreens("consult_screen")
    object VetsScreen : AppScreens("vets_screen")
}
