package com.nexappra.testapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexappra.testapp.data.model.ChatPreviewUi
import com.nexappra.testapp.data.model.ContactUi
import com.nexappra.testapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
        loadTemporaryHomeData()
    }

    fun selectTab(tab: HomeTab) {
        _uiState.update { currentState ->
            currentState.copy(selectedTab = tab)
        }
    }

    fun selectFilter(filter: ChatFilter) {
        _uiState.update { currentState ->
            currentState.copy(selectedFilter = filter)
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { currentState ->
            currentState.copy(searchQuery = query)
        }
    }

    fun clearSearch() {
        _uiState.update { currentState ->
            currentState.copy(searchQuery = "")
        }
    }

    fun clearError() {
        _uiState.update { currentState ->
            currentState.copy(errorMessage = null)
        }
    }

    fun logout() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            authRepository.logout().fold(
                onSuccess = {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            isLoggedOut = true
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = error.message
                                ?: "Unable to log out right now."
                        )
                    }
                }
            )
        }
    }

    fun onLogoutHandled() {
        _uiState.update { currentState ->
            currentState.copy(isLoggedOut = false)
        }
    }

    private fun loadCurrentUser() {
        val user = authRepository.currentUser

        if (user == null) {
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    errorMessage = "No authenticated user was found.",
                    isLoggedOut = true
                )
            }
            return
        }

        val userName = user.displayName
            ?.takeIf { name -> name.isNotBlank() }
            ?: user.email
                ?.substringBefore("@")
                .orEmpty()
                .ifBlank { "User" }

        _uiState.update { currentState ->
            currentState.copy(
                displayName = userName,
                email = user.email.orEmpty(),
                profileImageUrl = user.photoUrl?.toString(),
                isLoading = false,
                errorMessage = null
            )
        }
    }

    private fun loadTemporaryHomeData() {
        val sophia = ContactUi(
            id = "sophia",
            name = "Sophia Chen",
            status = "Available",
            isOnline = true
        )

        val marcus = ContactUi(
            id = "marcus",
            name = "Marcus Reid",
            status = "At work",
            isOnline = true
        )

        val elena = ContactUi(
            id = "elena",
            name = "Elena Vasquez",
            status = "Hey there!",
            isOnline = true
        )

        val daniel = ContactUi(
            id = "daniel",
            name = "Daniel Kim",
            status = "Busy",
            isOnline = false
        )

        val designTeam = ContactUi(
            id = "design_team",
            name = "Design Team",
            status = "5 members",
            isOnline = false
        )

        val james = ContactUi(
            id = "james",
            name = "James Walker",
            status = "Last seen yesterday",
            isOnline = false
        )

        val contacts = listOf(
            sophia,
            marcus,
            elena,
            daniel,
            designTeam,
            james
        )

        
        val chats = listOf(
            ChatPreviewUi(
                id = "chat_sophia",
                contact = sophia,
                lastMessage = "Typing...",
                time = "9:32 AM",
                unreadCount = 3,
                isTyping = true,
                isPinned = true
            ),
            ChatPreviewUi(
                id = "chat_marcus",
                contact = marcus,
                lastMessage = "Sounds good, talk soon!",
                time = "Yesterday",
                isPinned = true
            ),
            ChatPreviewUi(
                id = "chat_elena",
                contact = elena,
                lastMessage = "Voice message · 0:24",
                time = "8:14 AM",
                unreadCount = 1
            ),
            ChatPreviewUi(
                id = "chat_daniel",
                contact = daniel,
                lastMessage = "Photo",
                time = "Monday"
            ),
            ChatPreviewUi(
                id = "chat_design_team",
                contact = designTeam,
                lastMessage = "Priya: Specs_v2.pdf",
                time = "Sunday",
                isGroup = true
            ),
            ChatPreviewUi(
                id = "chat_james",
                contact = james,
                lastMessage = "Missed audio call",
                time = "Saturday"
            )
        )

        _uiState.update { currentState ->
            currentState.copy(
                contacts = contacts,
                chats = chats
            )
        }
    }
}