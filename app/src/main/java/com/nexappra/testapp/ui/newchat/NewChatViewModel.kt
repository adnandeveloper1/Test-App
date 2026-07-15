package com.nexappra.testapp.ui.newchat

import androidx.lifecycle.ViewModel
import com.nexappra.testapp.data.model.ContactUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NewChatViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        NewChatUiState(
            contacts = createTemporaryContacts()
        )
    )

    val uiState: StateFlow<NewChatUiState> =
        _uiState.asStateFlow()

    fun updateSearch(query: String) {
        _uiState.update { currentState ->
            currentState.copy(searchQuery = query)
        }
    }

    fun clearSearch() {
        _uiState.update { currentState ->
            currentState.copy(searchQuery = "")
        }
    }

    private fun createTemporaryContacts(): List<ContactUi> {
        return listOf(
            ContactUi(
                id = "sophia",
                name = "Sophia Chen",
                status = "Available",
                isOnline = true
            ),
            ContactUi(
                id = "marcus",
                name = "Marcus Reid",
                status = "At work",
                isOnline = true
            ),
            ContactUi(
                id = "elena",
                name = "Elena Vasquez",
                status = "Hey there!",
                isOnline = true
            ),
            ContactUi(
                id = "daniel",
                name = "Daniel Kim",
                status = "Busy",
                isOnline = false
            ),
            ContactUi(
                id = "james",
                name = "James Walker",
                status = "Last seen yesterday",
                isOnline = false
            )
        )
    }
}