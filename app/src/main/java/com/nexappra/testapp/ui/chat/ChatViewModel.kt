package com.nexappra.testapp.ui.chat

import androidx.lifecycle.ViewModel
import com.nexappra.testapp.data.model.MessageKind
import com.nexappra.testapp.data.model.MessageUi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChatViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        ChatUiState(
            messages = createTemporaryMessages()
        )
    )

    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun sendMessage(text: String) {
        val cleanText = text.trim()

        if (cleanText.isBlank()) {
            return
        }

        val currentTime = System.currentTimeMillis()

        val newMessage = MessageUi(
            id = UUID.randomUUID().toString(),
            senderId = CURRENT_USER_ID,
            receiverId = OTHER_USER_ID,
            text = cleanText,
            time = formatMessageTime(currentTime),
            timestamp = currentTime,
            isMine = true,
            kind = MessageKind.TEXT
        )

        _uiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages + newMessage
            )
        }
    }

    fun addReaction(
        messageId: String,
        reaction: String
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages.map { message ->
                    if (message.id == messageId) {
                        message.copy(reaction = reaction)
                    } else {
                        message
                    }
                }
            )
        }
    }

    fun toggleStar(messageId: String) {
        _uiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages.map { message ->
                    if (message.id == messageId) {
                        message.copy(
                            starred = !message.starred
                        )
                    } else {
                        message
                    }
                }
            )
        }
    }

    fun deleteMessage(messageId: String) {
        _uiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages.filterNot { message ->
                    message.id == messageId
                }
            )
        }
    }

    private fun formatMessageTime(timestamp: Long): String {
        return SimpleDateFormat(
            "h:mm a",
            Locale.getDefault()
        ).format(Date(timestamp))
    }

    private fun createTemporaryMessages(): List<MessageUi> {
        return listOf(
            MessageUi(
                id = "1",
                senderId = OTHER_USER_ID,
                receiverId = CURRENT_USER_ID,
                text = "Hey, are we still on for tomorrow?",
                time = "9:12 AM",
                timestamp = 1L,
                isMine = false,
                kind = MessageKind.TEXT
            ),
            MessageUi(
                id = "2",
                senderId = CURRENT_USER_ID,
                receiverId = OTHER_USER_ID,
                text = "Yes, I’ll send the final notes tonight.",
                time = "9:14 AM",
                timestamp = 2L,
                isMine = true,
                kind = MessageKind.TEXT
            ),
            MessageUi(
                id = "3",
                senderId = OTHER_USER_ID,
                receiverId = CURRENT_USER_ID,
                text = "I reviewed the proposal and the timeline looks good. We should probably align on the launch checklist.",
                time = "9:18 AM",
                timestamp = 3L,
                isMine = false,
                kind = MessageKind.TEXT
            ),
            MessageUi(
                id = "4",
                senderId = CURRENT_USER_ID,
                receiverId = OTHER_USER_ID,
                time = "9:21 AM",
                timestamp = 4L,
                isMine = true,
                kind = MessageKind.IMAGE
            ),
            MessageUi(
                id = "5",
                senderId = OTHER_USER_ID,
                receiverId = CURRENT_USER_ID,
                time = "9:24 AM",
                timestamp = 5L,
                isMine = false,
                kind = MessageKind.FILE,
                fileName = "Priya-Specs_v2.pdf",
                fileSize = "2.4 MB"
            ),
            MessageUi(
                id = "6",
                senderId = CURRENT_USER_ID,
                receiverId = OTHER_USER_ID,
                time = "9:26 AM",
                timestamp = 6L,
                isMine = true,
                kind = MessageKind.VOICE,
                duration = "0:31"
            )
        )
    }

    private companion object {
        const val CURRENT_USER_ID = "me"
        const val OTHER_USER_ID = "other"
    }
}