package com.nexappra.testapp.data.model

data class ContactUi(
    val id: String = "",
    val name: String = "",
    val status: String = "",
    val profileImageUrl: String? = null,
    val isOnline: Boolean = false
)

data class ChatPreviewUi(
    val id: String = "",
    val contact: ContactUi = ContactUi(),
    val lastMessage: String = "",
    val time: String = "",
    val unreadCount: Int = 0,
    val isTyping: Boolean = false,
    val isPinned: Boolean = false,
    val isGroup: Boolean = false,
    val isArchived: Boolean = false,
    val isMuted: Boolean = false
)