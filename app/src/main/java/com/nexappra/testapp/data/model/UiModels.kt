package com.nexappra.testapp.data.model

enum class MessageKind {
    TEXT,
    IMAGE,
    FILE,
    VOICE
}

data class MessageUi(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val time: String = "",
    val timestamp: Long = 0L,
    val isMine: Boolean = false,
    val kind: MessageKind = MessageKind.TEXT,
    val fileName: String? = null,
    val fileSize: String? = null,
    val duration: String? = null,
    val reaction: String? = null,
    val starred: Boolean = false
)