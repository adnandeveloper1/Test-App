package com.nexappra.testapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {
        return runCatching {
            firebaseAuth
                .signInWithEmailAndPassword(
                    email.trim(),
                    password
                )
                .await()

            Unit
        }
    }

    override suspend fun createAccount(
        name: String,
        email: String,
        password: String
    ): Result<Unit> {
        return runCatching {
            val authResult = firebaseAuth
                .createUserWithEmailAndPassword(
                    email.trim(),
                    password
                )
                .await()

            val user = authResult.user
                ?: error("User account could not be created.")

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name.trim())
                .build()

            user.updateProfile(profileUpdates).await()

            Unit
        }
    }

    override suspend fun sendPasswordResetEmail(
        email: String
    ): Result<Unit> {
        return runCatching {
            firebaseAuth
                .sendPasswordResetEmail(email.trim())
                .await()

            Unit
        }
    }

    override suspend fun logout(): Result<Unit> {
        return runCatching {
            firebaseAuth.signOut()
            Unit
        }
    }
}