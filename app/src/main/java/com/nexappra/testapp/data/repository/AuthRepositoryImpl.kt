package com.nexappra.testapp.data.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override suspend fun createAccount(
        name: String,
        email: String,
        password: String
    ): Result<Unit> {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()

        return try {
            val authResult = auth.createUserWithEmailAndPassword(trimmedEmail, password).await()
            val user = authResult.user ?: error("Unable to create your account right now.")

            try {
                saveUserProfile(user = user, name = trimmedName, email = trimmedEmail)
                updateDisplayName(user, trimmedName)
                Result.success(Unit)
            } catch (error: Exception) {
                val rollbackSucceeded = rollbackAccountCreation(user)
                val message = if (rollbackSucceeded) {
                    mapErrorToMessage(error)
                } else {
                    "Account setup failed and automatic cleanup could not finish. Please try signing in again before retrying."
                }
                Result.failure(IllegalStateException(message))
            }
        } catch (error: Exception) {
            Result.failure(IllegalStateException(mapErrorToMessage(error)))
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email.trim(), password).await()
            Result.success(Unit)
        } catch (error: Exception) {
            Result.failure(IllegalStateException(mapErrorToMessage(error)))
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (error: Exception) {
            Result.failure(IllegalStateException(mapErrorToMessage(error)))
        }
    }

    override suspend fun logout(): Result<Unit> {
        return runCatching {
            auth.signOut()
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { error ->
                Result.failure(IllegalStateException(mapErrorToMessage(error)))
            }
        )
    }

    override fun isUserLoggedIn(): Boolean = auth.currentUser != null

    private suspend fun saveUserProfile(
        user: FirebaseUser,
        name: String,
        email: String
    ) {
        val userData = hashMapOf(
            "uid" to user.uid,
            "name" to name,
            "email" to email,
            "createdAt" to FieldValue.serverTimestamp()
        )

        firestore.collection(USERS_COLLECTION).document(user.uid).set(userData).await()
    }

    private suspend fun updateDisplayName(
        user: FirebaseUser,
        name: String
    ) {
        val profileUpdates: UserProfileChangeRequest = userProfileChangeRequest {
            displayName = name
        }
        user.updateProfile(profileUpdates).await()
    }

    private suspend fun rollbackAccountCreation(user: FirebaseUser): Boolean {
        runCatching {
            firestore.collection(USERS_COLLECTION).document(user.uid).delete().await()
        }

        val deleteResult = runCatching {
            user.delete().await()
        }

        if (auth.currentUser?.uid == user.uid) {
            auth.signOut()
        }

        return deleteResult.isSuccess
    }

    private fun mapErrorToMessage(error: Throwable): String {
        val errorCode = (error as? FirebaseAuthException)
            ?.errorCode
            ?.lowercase()
            ?.replace('_', '-')
            ?.removePrefix("error-")

        return when {
            error is FirebaseNetworkException ||
                errorCode == "network-request-failed" -> {
                "Network error. Please check your internet connection and try again."
            }

            errorCode == "operation-not-allowed" -> {
                "Email and password authentication is disabled in Firebase. Enable it from Firebase Console > Authentication > Sign-in method."
            }

            errorCode == "email-already-in-use" -> {
                "This email address is already in use."
            }

            errorCode == "invalid-email" -> {
                "Please enter a valid email address."
            }

            errorCode == "weak-password" -> {
                "Password must be at least 6 characters."
            }

            errorCode == "user-not-found" -> {
                "No account was found with that email address."
            }

            errorCode == "wrong-password" ||
                errorCode == "invalid-credential" -> {
                "Incorrect email or password."
            }

            errorCode == "user-disabled" -> {
                "This account has been disabled."
            }

            errorCode == "too-many-requests" -> {
                "Too many attempts were made. Please wait a moment and try again."
            }

            error is FirebaseFirestoreException &&
                error.code == FirebaseFirestoreException.Code.PERMISSION_DENIED -> {
                "You do not have permission to access this account data."
            }

            error is FirebaseFirestoreException &&
                error.code == FirebaseFirestoreException.Code.NOT_FOUND -> {
                "Cloud Firestore is not set up yet. Create the Firestore database in Firebase Console and try again."
            }

            error is FirebaseFirestoreException &&
                error.code == FirebaseFirestoreException.Code.UNAVAILABLE -> {
                "Cloud Firestore is currently unavailable. Please try again."
            }

            error is FirebaseFirestoreException -> {
                "We could not save your profile right now. Please try again."
            }

            else -> "Something went wrong. Please try again."
        }
    }

    private companion object {
        private const val USERS_COLLECTION = "users"
    }
}
