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
    object ResetPasswordScreen : AppScreens("reset_password_screen")
    object UpdatePasswordScreen : AppScreens("update_password_screen")
    object AuthenticationScreen : AppScreens("authentication_screen")
}
