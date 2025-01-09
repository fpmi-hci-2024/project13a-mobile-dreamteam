package com.example.brainbridge.Auth

import com.google.firebase.auth.FirebaseUser

class AuthController(private val authRepository: AuthRepository) {

    fun checkCurrentUser(): FirebaseUser? {
        return authRepository.getCurrentUser()
    }

    fun login(email: String, password: String, callback: (Result<FirebaseUser>) -> Unit) {
        authRepository.signIn(email, password, callback)
    }

    fun register(email: String, password: String, confirmPassword: String, callback: (Result<FirebaseUser>) -> Unit) {
        if (password != confirmPassword) {
            callback(Result.failure(Exception("Passwords do not match")))
            return
        }
        authRepository.register(email, password, callback)
    }
}