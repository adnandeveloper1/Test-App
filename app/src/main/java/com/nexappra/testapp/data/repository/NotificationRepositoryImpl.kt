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

                    if (task.isSuccessful) {
                        val token = task.result

                        if (token.isNullOrBlank()) {
                            continuation.resume(
                                Result.failure(
                                    IllegalStateException(
                                        "Firebase returned an empty FCM token."
                                    )
                                )
                            )
                        } else {
                            continuation.resume(
                                Result.success(token)
                            )
                        }
                    } else {
                        continuation.resume(
                            Result.failure(
                                task.exception
                                    ?: IllegalStateException(
                                        "Unable to retrieve FCM token."
                                    )
                            )
                        )
                    }
                }
        }
    }
}