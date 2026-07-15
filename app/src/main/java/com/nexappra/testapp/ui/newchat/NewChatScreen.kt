package com.nexappra.testapp.ui.newchat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexappra.testapp.ui.components.AppAvatar
import com.nexappra.testapp.ui.theme.NexaBackground
import com.nexappra.testapp.ui.theme.NexaDivider
import com.nexappra.testapp.ui.theme.NexaGreen
import com.nexappra.testapp.ui.theme.NexaSecondaryText
import com.nexappra.testapp.ui.theme.NexaSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChatScreen(
    onBack: () -> Unit,
    onContactSelected: (String, String) -> Unit,
    viewModel: NewChatViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = NexaBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "New Chat",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // QR scanner will be added later.
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.QrCodeScanner,
                            contentDescription = "Scan QR"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NexaBackground
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::updateSearch,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = "Search contacts")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search"
                    )
                },
                singleLine = true,
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = NexaSurface,
                    focusedContainerColor = NexaSurface,
                    unfocusedBorderColor = NexaDivider,
                    focusedBorderColor = NexaGreen
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // New-group navigation will be added later.
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = NexaSurface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.GroupAdd,
                        contentDescription = "Start new group"
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Start new group",
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Create a group chat with multiple contacts",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexaSecondaryText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "FREQUENTLY CONTACTED",
                style = MaterialTheme.typography.labelMedium,
                color = NexaSecondaryText
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.horizontalScroll(
                    rememberScrollState()
                ),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                state.contacts
                    .take(4)
                    .forEach { contact ->

                        Column(
                            modifier = Modifier
                                .width(70.dp)
                                .clickable {
                                    onContactSelected(
                                        contact.id,
                                        contact.name
                                    )
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AppAvatar(
                                initials = createInitials(contact.name),
                                online = contact.isOnline,
                                size = 54.dp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = contact.name.substringBefore(" "),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                        }
                    }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "RECENT",
                style = MaterialTheme.typography.labelMedium,
                color = NexaSecondaryText
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (state.filteredContacts.isEmpty()) {
                Text(
                    text = "No contacts found.",
                    modifier = Modifier.padding(vertical = 24.dp),
                    color = NexaSecondaryText
                )
            } else {
                state.filteredContacts.forEach { contact ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onContactSelected(
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
                                color = NexaSecondaryText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

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