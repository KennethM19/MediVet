package com.example.medivet.utils

import com.google.firebase.auth.FirebaseAuthException

fun getFirebaseErrorMessage(exception: Exception?): String {
    val errorCode = (exception as? FirebaseAuthException)?.errorCode

    return when (errorCode) {
        "ERROR_INVALID_EMAIL" -> "El formato del correo no es válido."
        "ERROR_USER_NOT_FOUND" -> "El usuario no está registrado."
        "ERROR_WRONG_PASSWORD" -> "La contraseña es incorrecta."
        "ERROR_USER_DISABLED" -> "La cuenta ha sido deshabilitada."
        "ERROR_EMAIL_ALREADY_IN_USE" -> "El correo ya está en uso."
        "ERROR_WEAK_PASSWORD" -> "La contraseña es muy débil."
        else -> "Error al procesar la autenticación."
    }
}