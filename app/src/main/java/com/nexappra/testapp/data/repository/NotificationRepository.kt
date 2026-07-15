package com.nexappra.testapp.data.repository

interface NotificationRepository {
    suspend fun getFcmToken(): Result<String>
}