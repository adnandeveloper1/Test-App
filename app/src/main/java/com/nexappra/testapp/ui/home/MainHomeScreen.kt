package com.nexappra.testapp.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexappra.testapp.data.model.ChatPreviewUi
import com.nexappra.testapp.data.model.ContactUi
import com.nexappra.testapp.ui.components.AppAvatar

private val NexaGreen = Color(0xFF00A884)
private val NexaLightGreen = Color(0xFFD9FDD3)
private val ScreenBackground = Color(0xFFF7F7F8)
private val DividerColor = Color(0xFFE8E8EA)
private val SecondaryText = Color(0xFF73777B)
private val PrimaryText = Color(0xFF161616)

@Composable
fun MainHomeScreen(
    onOpenChat: (contactId: String, contactName: String) -> Unit,
    onNewChat: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            viewModel.onLogoutHandled()
            onLoggedOut()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = ScreenBackground,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            HomeBottomNavigation(
                selectedTab = state.selectedTab,
                onTabSelected = { tab ->
                    viewModel.selectTab(tab)
                }
            )
        },
        floatingActionButton = {
            if (state.selectedTab == HomeTab.CHATS) {
                FloatingActionButton(
                    onClick = onNewChat,
                    containerColor = NexaGreen,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(17.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New chat"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (state.selectedTab) {
                HomeTab.CHATS -> {
                    ChatsTab(
                        state = state,
                        onSearchQueryChange = { query ->
                            viewModel.updateSearchQuery(query)
                        },
                        onClearSearch = {
                            viewModel.clearSearch()
                        },
                        onFilterSelected = { filter ->
                            viewModel.selectFilter(filter)
                        },
                        onOpenChat = onOpenChat
                    )
                }

                HomeTab.CALLS -> {
                    EmptyTabScreen(
                        title = "Calls",
                        message = "Your audio and video calls will appear here.",
                        icon = Icons.Default.Call
                    )
                }

                HomeTab.CONTACTS -> {
                    ContactsTab(
                        contacts = state.contacts,
                        onOpenChat = onOpenChat
                    )
                }

                HomeTab.PROFILE -> {
                    ProfileTab(
                        state = state,
                        onLogout = {
                            viewModel.logout()
                        }
                    )
                }
            }

            if (state.isLoading) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black.copy(alpha = 0.25f)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NexaGreen)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatsTab(
    state: HomeUiState,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onFilterSelected: (ChatFilter) -> Unit,
    onOpenChat: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
    ) {
        HomeHeader(displayName = state.displayName)

        Spacer(modifier = Modifier.height(10.dp))

        SearchField(
            query = state.searchQuery,
            onQueryChange = onSearchQueryChange,
            onClear = onClearSearch
        )

        Spacer(modifier = Modifier.height(10.dp))

        FilterRow(
            selectedFilter = state.selectedFilter,
            onFilterSelected = onFilterSelected
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (state.filteredChats.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No conversations found.",
                    color = SecondaryText
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(
                    items = state.filteredChats,
                    key = { chat -> chat.id }
                ) { chat ->
                    ChatItem(
                        chat = chat,
                        onClick = {
                            onOpenChat(
                                chat.contact.id,
                                chat.contact.name
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    displayName: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(38.dp),
            color = PrimaryText,
            shape = RoundedCornerShape(10.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "Nexa",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Nexa",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (displayName.isNotBlank()) {
                Text(
                    text = "Welcome, $displayName",
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText
                )
            }
        }

        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications"
            )
        }

        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options"
            )
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(text = "Search messages")
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        singleLine = true,
        shape = CircleShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NexaGreen,
            unfocusedBorderColor = DividerColor,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
private fun FilterRow(
    selectedFilter: ChatFilter,
    onFilterSelected: (ChatFilter) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        ChatFilter.values().forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = {
                    onFilterSelected(filter)
                },
                label = {
                    Text(
                        text = filter.name
                            .lowercase()
                            .replaceFirstChar { character ->
                                character.uppercase()
                            }
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryText,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun ChatItem(
    chat: ChatPreviewUi,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppAvatar(
            initials = createInitials(chat.contact.name),
            online = chat.contact.isOnline
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.contact.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = chat.time,
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.lastMessage,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (chat.isTyping) {
                        NexaGreen
                    } else {
                        SecondaryText
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (chat.unreadCount > 0) {
                    Badge(
                        containerColor = PrimaryText,
                        contentColor = Color.White
                    ) {
                        Text(text = chat.unreadCount.toString())
                    }
                }
            }
        }
    }

    HorizontalDivider(
        modifier = Modifier.padding(start = 62.dp),
        color = DividerColor
    )
}

@Composable
private fun ContactsTab(
    contacts: List<ContactUi>,
    onOpenChat: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
    ) {
        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Contacts",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (contacts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No contacts found.",
                    color = SecondaryText
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(
                    items = contacts,
                    key = { contact -> contact.id }
                ) { contact ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onOpenChat(
                                    contact.id,
                                    contact.name
                                )
                            }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppAvatar(
                            initials = createInitials(contact.name),
                            online = contact.isOnline
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = contact.name,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = contact.status,
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryText
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(start = 62.dp),
                        color = DividerColor
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileTab(
    state: HomeUiState,
    onLogout: () -> Unit
) {
    val displayName = state.displayName.ifBlank { "User" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        AppAvatar(
            initials = createInitials(displayName),
            online = true,
            size = 96.dp
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = displayName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = state.email,
            color = SecondaryText
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = onLogout,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Log out"
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(text = "Log out")
        }
    }
}

@Composable
private fun EmptyTabScreen(
    title: String,
    message: String,
    icon: ImageVector
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier
                .size(72.dp)
                .align(Alignment.CenterHorizontally),
            tint = SecondaryText
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = SecondaryText
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun HomeBottomNavigation(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit
) {
    val navigationItems = listOf(
        BottomNavigationItem(
            tab = HomeTab.CHATS,
            title = "Chats",
            icon = Icons.Default.Chat
        ),
        BottomNavigationItem(
            tab = HomeTab.CALLS,
            title = "Calls",
            icon = Icons.Default.Call
        ),
        BottomNavigationItem(
            tab = HomeTab.CONTACTS,
            title = "Contacts",
            icon = Icons.Default.Group
        ),
        BottomNavigationItem(
            tab = HomeTab.PROFILE,
            title = "Profile",
            icon = Icons.Default.Person
        )
    )

    NavigationBar(containerColor = Color.White) {
        navigationItems.forEach { item ->
            NavigationBarItem(
                selected = selectedTab == item.tab,
                onClick = {
                    onTabSelected(item.tab)
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(text = item.title)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NexaGreen,
                    selectedTextColor = NexaGreen,
                    indicatorColor = NexaLightGreen
                )
            )
        }
    }
}

private data class BottomNavigationItem(
    val tab: HomeTab,
    val title: String,
    val icon: ImageVector
)

private fun createInitials(name: String): String {
    return name
        .trim()
        .split(" ")
        .filter { word -> word.isNotBlank() }
        .take(2)
        .joinToString(separator = "") { word ->
            word.firstOrNull()
                ?.uppercase()
                .orEmpty()
        }
        .ifBlank { "U" }
}