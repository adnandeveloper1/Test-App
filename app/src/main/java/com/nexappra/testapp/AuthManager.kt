package com.nexappra.testapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import java.io.IOException

object AuthManager {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    suspend fun createAccount(
        personalInfo: PersonalInfo,
        secureInfo: SecureAccountInfo
    ): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(
                personalInfo.email,
                secureInfo.password
            ).await()

            val user = authResult.user ?: throw Exception("User creation failed.")

            val userProfile = hashMapOf(
                "uid" to user.uid,
                "firstName" to personalInfo.firstName,
                "lastName" to personalInfo.lastName,
                "email" to personalInfo.email,
                "username" to personalInfo.username,
                "birthdayDay" to secureInfo.day,
                "birthdayMonth" to secureInfo.month,
                "birthdayYear" to secureInfo.year,
                "phoneNumber" to secureInfo.phoneNumber,
                "securityQuestion" to secureInfo.securityQuestion,
                "createdAt" to FieldValue.serverTimestamp()
            )

            try {
                firestore.collection("users").document(user.uid).set(userProfile).await()
                auth.signOut()
                Result.success(Unit)
            } catch (e: Exception) {
                // If Firestore fails, delete the Auth user
                user.delete().await()
                throw e
            }
        } catch (e: Exception) {
            Result.failure(Exception(getFriendlyErrorMessage(e)))
        }
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(getFriendlyErrorMessage(e)))
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(getFriendlyErrorMessage(e)))
        }
    }

    private fun getFriendlyErrorMessage(e: Exception): String {
        return when (e) {
            is FirebaseAuthUserCollisionException -> "Email already registered."
            is FirebaseAuthWeakPasswordException -> "The password is too weak."
            is FirebaseAuthInvalidCredentialsException -> "Invalid email or wrong password."
            is FirebaseAuthInvalidUserException -> "No account found with this email."
            is FirebaseFirestoreException -> {
                if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                    "Firestore permission denied."
                } else {
                    "Database error: ${e.localizedMessage}"
                }
            }
            is IOException -> "No internet connection. Please check your network."
            else -> e.localizedMessage ?: "An unexpected error occurred."
        }
    }
}
