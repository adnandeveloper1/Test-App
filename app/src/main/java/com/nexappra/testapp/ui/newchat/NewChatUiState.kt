package com.nexappra.testapp.ui.newchat

import com.nexappra.testapp.data.model.ContactUi

data class NewChatUiState(
    val searchQuery: String = "",
    val contacts: List<ContactUi> = emptyList()
) {
    val filteredContacts: List<ContactUi>
        get() {
            val query = searchQuery.trim()

            if (query.isBlank()) {
                return contacts
            }

            return contacts.filter { contact ->
                contact.name.contains(
                    other = query,
                    ignoreCase = true
                ) || contact.status.contains(
                    other = query,
                    ignoreCase = true
                )
            }
        }
}