package com.nexappra.testapp.ui.chat

import com.nexappra.testapp.data.model.MessageUi

data class ChatUiState(
    val messages: List<MessageUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)