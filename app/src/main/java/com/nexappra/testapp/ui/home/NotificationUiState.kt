package com.nexappra.testapp.ui.home

data class NotificationUiState(
    val isLoading: Boolean = false,
    val fcmToken: String = "",
    val errorMessage: String? = null,
    val notificationPermissionGranted: Boolean = false
)