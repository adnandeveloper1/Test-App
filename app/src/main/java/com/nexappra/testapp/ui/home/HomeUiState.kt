package com.nexappra.testapp.ui.home

import com.nexappra.testapp.data.model.ChatPreviewUi
import com.nexappra.testapp.data.model.ContactUi

enum class HomeTab {
    CHATS,
    CALLS,
    CONTACTS,
    PROFILE
}

enum class ChatFilter {
    ALL,
    UNREAD,
    GROUPS,
    ARCHIVED
}

data class HomeUiState(
    val displayName: String = "",
    val email: String = "",
    val profileImageUrl: String? = null,

    val selectedTab: HomeTab = HomeTab.CHATS,
    val selectedFilter: ChatFilter = ChatFilter.ALL,
    val searchQuery: String = "",

    val chats: List<ChatPreviewUi> = emptyList(),
    val contacts: List<ContactUi> = emptyList(),

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedOut: Boolean = false
) {

    val filteredChats: List<ChatPreviewUi>
        get() {
            val normalizedQuery = searchQuery.trim()

            return chats.filter { chat ->

                val matchesSearch =
                    normalizedQuery.isBlank() ||
                            chat.contact.name.contains(
                                other = normalizedQuery,
                                ignoreCase = true
                            ) ||
                            chat.lastMessage.contains(
                                other = normalizedQuery,
                                ignoreCase = true
                            )

                val matchesFilter = when (selectedFilter) {
                    ChatFilter.ALL -> {
                        !chat.isArchived
                    }

                    ChatFilter.UNREAD -> {
                        !chat.isArchived && chat.unreadCount > 0
                    }

                    ChatFilter.GROUPS -> {
                        !chat.isArchived && chat.isGroup
                    }

                    ChatFilter.ARCHIVED -> {
                        chat.isArchived
                    }
                }

                matchesSearch && matchesFilter
            }
        }
}