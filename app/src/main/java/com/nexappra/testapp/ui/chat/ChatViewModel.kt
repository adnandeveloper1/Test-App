package com.nexappra.testapp.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val savedContactId: String =
        savedStateHandle["contactId"] ?: ""

    private val savedContactName: String =
        savedStateHandle["contactName"] ?: "Contact"

    private val _uiState = MutableStateFlow(
        ChatUiState(
            contactId = savedContactId,
            contactName = savedContactName,
            isOnline = true,
            messages = createTemporaryMessages()
        )
    )

    val uiState: StateFlow<ChatUiState> =
        _uiState.asStateFlow()

    fun initialize(
        contactId: String,
        contactName: String
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                contactId = contactId,
                contactName = contactName.ifBlank {
                    "Contact"
                }
            )
        }
    }

    fun updateDraftMessage(message: String) {
        _uiState.update { currentState ->
            currentState.copy(
                draftMessage = message
            )
        }
    }

    fun sendMessage() {
        val messageText =
            _uiState.value.draftMessage.trim()

        if (messageText.isBlank()) {
            return
        }

        val newMessage = ChatMessageUi(
            id = System.currentTimeMillis().toString(),
            text = messageText,
            time = currentTime(),
            isMine = true,
            type = ChatMessageType.TEXT
        )

        _uiState.update { currentState ->
            currentState.copy(
                draftMessage = "",
                messages = currentState.messages + newMessage
            )
        }
    }

    private fun currentTime(): String {
        return SimpleDateFormat(
            "h:mm a",
            Locale.getDefault()
        ).format(Date())
    }

    private fun createTemporaryMessages(): List<ChatMessageUi> {
        return listOf(
            ChatMessageUi(
                id = "system_1",
                text = "Messages are end-to-end encrypted",
                type = ChatMessageType.SYSTEM
            ),
            ChatMessageUi(
                id = "system_2",
                text = "Missed audio call",
                type = ChatMessageType.SYSTEM
            ),
            ChatMessageUi(
                id = "system_3",
                text = "Forwarded",
                type = ChatMessageType.SYSTEM
            ),
            ChatMessageUi(
                id = "message_1",
                text = "Hey, are we still on for tomorrow?",
                time = "9:12 AM",
                isMine = false
            ),
            ChatMessageUi(
                id = "message_2",
                text = "Yes, I’ll send the final notes tonight.",
                time = "9:14 AM",
                isMine = true
            ),
            ChatMessageUi(
                id = "message_3",
                text = "I reviewed the proposal and the timeline looks good. " +
                        "We should probably align on the launch checklist, " +
                        "confirm the assets, and make sure the QA pass is complete.",
                time = "9:18 AM",
                isMine = false
            ),
            ChatMessageUi(
                id = "message_4",
                text = "😍 🔥",
                time = "9:20 AM",
                isMine = true
            ),
            ChatMessageUi(
                id = "media_1",
                time = "9:22 AM",
                isMine = false,
                type = ChatMessageType.IMAGE_GRID
            ),
            ChatMessageUi(
                id = "saved_1",
                text = "Saved for later",
                time = "9:23 AM",
                isMine = false,
                type = ChatMessageType.SAVED
            )
        )
    }
}