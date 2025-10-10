package com.example.medivet.repository


import com.example.medivet.model.User
import com.google.firebase.auth.FirebaseAuth

class UserRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser
        return firebaseUser?.let {
            User(
                name = it.displayName ?: "[Nombre]",
                email = it.email ?: "example@example.com",
                profileImageUrl = it.photoUrl?.toString()
            )
        }
    }

    fun signOut() {
        auth.signOut()
    }
}