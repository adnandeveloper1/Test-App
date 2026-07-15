package com.nexappra.testapp.ui.chat

enum class ChatMessageType {
    TEXT,
    SYSTEM,
    IMAGE_GRID,
    SAVED
}

data class ChatMessageUi(
    val id: String,
    val text: String = "",
    val time: String = "",
    val isMine: Boolean = false,
    val type: ChatMessageType = ChatMessageType.TEXT
)

data class ChatUiState(
    val contactId: String = "",
    val contactName: String = "",
    val isOnline: Boolean = true,
    val draftMessage: String = "",
    val messages: List<ChatMessageUi> = emptyList()
)