package com.nexappra.testapp.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexappra.testapp.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository:
    NotificationRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(NotificationUiState())

    val uiState: StateFlow<NotificationUiState> =
        _uiState.asStateFlow()

    init {
        loadFcmToken()
    }

    fun loadFcmToken() {
        if (_uiState.value.isLoading) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result =
                notificationRepository.getFcmToken()

            result.onSuccess { token ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    fcmToken = token,
                    errorMessage = null
                )
            }

            result.onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    fcmToken = "",
                    errorMessage =
                        "Unable to retrieve the FCM token."
                )
            }
        }
    }

    fun onNotificationPermissionResult(
        isGranted: Boolean
    ) {
        _uiState.value = _uiState.value.copy(
            notificationPermissionGranted = isGranted
        )
    }
}