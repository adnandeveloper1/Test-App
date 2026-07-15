package com.nexappra.testapp.data.repository

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val firebaseMessaging: FirebaseMessaging
) : NotificationRepository {

    override suspend fun getFcmToken(): Result<String> {
        return suspendCancellableCoroutine { continuation ->

            firebaseMessaging.token
                .addOnCompleteListener { task ->

                    if (!continuation.isActive) {
                        return@addOnCompleteListener
                    }

                    val result: Result<String> = when {
                        task.isSuccessful &&
                                !task.result.isNullOrBlank() -> {

                            Result.success(task.result)
                        }

                        task.isSuccessful -> {
                            Result.failure(
                                IllegalStateException(
                                    "Firebase returned an empty FCM token."
                                )
                            )
                        }

                        else -> {
                            Result.failure(
                                task.exception
                                    ?: IllegalStateException(
                                        "Unable to retrieve FCM token."
                                    )
                            )
                        }
                    }

                    continuation.resume(result)
                }
        }
    }
}