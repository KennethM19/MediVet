package com.example.medivet.navigation

//Este fichero sirve para definir las diferentes pantallas entre las que podamos navegar
//Manejador de rutas

sealed class AppScreens(val route: String) {
    // Pantallas iniciales
    object SplashScreen : AppScreens("splash_screen")
    object MainScreen : AppScreens("main_screen")

    // Autenticación
    object LoginScreen : AppScreens("login_screen")
    object RegisterScreen : AppScreens("register_screen")
    object PasswordResetScreen : AppScreens("password_reset_screen")
    object UpdatePasswordScreen : AppScreens("update_password_screen")
    object AuthenticationScreen : AppScreens("authentication_screen")

    //Navegación
    object PetsScreen : AppScreens("pets_screen")
    object ProfileScreen : AppScreens("profile_screen")
    object InfoScreen : AppScreens("info_screen")
    object ConsultScreen : AppScreens("consult_screen")
    object VetsScreen : AppScreens("vets_screen")
}
