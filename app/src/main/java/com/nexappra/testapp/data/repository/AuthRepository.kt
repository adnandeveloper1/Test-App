package com.nexappra.testapp.data.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {

    val currentUser: FirebaseUser?

    suspend fun login(
        email: String,
        password: String
    ): Result<Unit>

    suspend fun createAccount(
        name: String,
        email: String,
        password: String
    ): Result<Unit>

    suspend fun sendPasswordResetEmail(
        email: String
    ): Result<Unit>

    suspend fun logout(): Result<Unit>
}