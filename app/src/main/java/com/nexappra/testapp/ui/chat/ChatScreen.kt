package com.nexappra.testapp.ui.chat

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.InsertEmoticon
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexappra.testapp.ui.components.AppAvatar

private val ChatGreen = Color(0xFF009F8C)
private val ChatBackground = Color(0xFFF7F7F8)
private val ReceivedBubble = Color(0xFFF1F1F2)
private val SystemBubble = Color(0xFFF1F1F1)
private val SecondaryText = Color(0xFF74777A)
private val DividerColor = Color(0xFFE6E6E8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    contactId: String,
    contactName: String,
    onBack: () -> Unit,
    onAudioCall: () -> Unit,
    onVideoCall: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(contactId, contactName) {
        viewModel.initialize(
            contactId = contactId,
            contactName = contactName
        )
    }

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(
                state.messages.lastIndex
            )
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        containerColor = ChatBackground,
        topBar = {
            ChatTopBar(
                contactName = state.contactName,
                isOnline = state.isOnline,
                onBack = onBack,
                onAudioCall = onAudioCall,
                onVideoCall = onVideoCall
            )
        },
        bottomBar = {
            MessageComposer(
                value = state.draftMessage,
                onValueChange = viewModel::updateDraftMessage,
                onSend = viewModel::sendMessage
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            state = listState,
            contentPadding = PaddingValues(
                horizontal = 14.dp,
                vertical = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = state.messages,
                key = { message ->
                    message.id
                }
            ) { message ->
                ChatMessageItem(message)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(
    contactName: String,
    isOnline: Boolean,
    onBack: () -> Unit,
    onAudioCall: () -> Unit,
    onVideoCall: () -> Unit
) {
    Column {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppAvatar(
                        initials = createInitials(contactName),
                        online = isOnline,
                        size = 42.dp
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = contactName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = if (isOnline) {
                                "Online"
                            } else {
                                "Offline"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isOnline) {
                                ChatGreen
                            } else {
                                SecondaryText
                            }
                        )
                    }
                }
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
                IconButton(onClick = onAudioCall) {
                    Icon(
                        imageVector = Icons.Outlined.Call,
                        contentDescription = "Audio call"
                    )
                }

                IconButton(onClick = onVideoCall) {
                    Icon(
                        imageVector = Icons.Outlined.Videocam,
                        contentDescription = "Video call"
                    )
                }

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = "More options"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        HorizontalDivider(color = DividerColor)
    }
}

@Composable
private fun ChatMessageItem(
    message: ChatMessageUi
) {
    when (message.type) {
        ChatMessageType.SYSTEM -> {
            SystemMessageChip(message.text)
        }

        ChatMessageType.TEXT -> {
            TextMessageBubble(message)
        }

        ChatMessageType.IMAGE_GRID -> {
            ImageGridMessage(message)
        }

        ChatMessageType.SAVED -> {
            SavedMessageCard(message)
        }
    }
}

@Composable
private fun SystemMessageChip(
    message: String
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = SystemBubble,
            shape = CircleShape
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 8.dp
                ),
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText
            )
        }
    }
}

@Composable
private fun TextMessageBubble(
    message: ChatMessageUi
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.isMine) {
            Alignment.CenterEnd
        } else {
            Alignment.CenterStart
        }
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 285.dp),
            color = if (message.isMine) {
                ChatGreen
            } else {
                ReceivedBubble
            },
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (message.isMine) {
                    18.dp
                } else {
                    4.dp
                },
                bottomEnd = if (message.isMine) {
                    4.dp
                } else {
                    18.dp
                }
            ),
            shadowElevation = if (message.isMine) {
                1.dp
            } else {
                2.dp
            }
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 13.dp,
                    vertical = 10.dp
                )
            ) {
                Text(
                    text = message.text,
                    color = if (message.isMine) {
                        Color.White
                    } else {
                        Color(0xFF202124)
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                if (message.time.isNotBlank()) {
                    Spacer(modifier = Modifier.height(5.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = message.time,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (message.isMine) {
                                Color.White.copy(alpha = 0.78f)
                            } else {
                                SecondaryText
                            }
                        )

                        if (message.isMine) {
                            Spacer(modifier = Modifier.width(4.dp))

                            Icon(
                                imageVector = Icons.Outlined.DoneAll,
                                contentDescription = "Delivered",
                                modifier = Modifier.size(15.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageGridMessage(
    message: ChatMessageUi
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Surface(
            color = ReceivedBubble,
            shape = RoundedCornerShape(18.dp),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(7.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    MediaTile()
                    MediaTile()
                }

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    MediaTile()
                    MediaTile()
                }

                if (message.time.isNotBlank()) {
                    Text(
                        text = message.time,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryText
                    )
                }
            }
        }
    }
}

@Composable
private fun MediaTile() {
    Box(
        modifier = Modifier
            .size(82.dp)
            .background(
                color = Color(0xFFD8DADD),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable {
                // Full-screen image preview will be added later.
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Image,
            contentDescription = "Image",
            modifier = Modifier.size(30.dp),
            tint = SecondaryText
        )
    }
}

@Composable
private fun SavedMessageCard(
    message: ChatMessageUi
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Surface(
            color = ReceivedBubble,
            shape = RoundedCornerShape(18.dp),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .widthIn(min = 150.dp)
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = "Saved",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = message.text,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (message.time.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = message.time,
                        modifier = Modifier.align(Alignment.End),
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryText
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageComposer(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        color = Color.White,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 10.dp,
                    vertical = 8.dp
                ),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(text = "Message")
                },
                leadingIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Outlined.InsertEmoticon,
                            contentDescription = "Emoji"
                        )
                    }
                },
                trailingIcon = {
                    Row {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Outlined.AttachFile,
                                contentDescription = "Attach file"
                            )
                        }

                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Outlined.CameraAlt,
                                contentDescription = "Camera"
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(28.dp),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ChatGreen,
                    unfocusedBorderColor = DividerColor,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                modifier = Modifier
                    .size(52.dp)
                    .clickable {
                        if (value.isNotBlank()) {
                            onSend()
                        }
                    },
                shape = CircleShape,
                color = ChatGreen
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (value.isBlank()) {
                            Icons.Outlined.Mic
                        } else {
                            Icons.Outlined.Send
                        },
                        contentDescription = if (value.isBlank()) {
                            "Voice message"
                        } else {
                            "Send message"
                        },
                        tint = Color.White
                    )
                }
            }
        }
    }
}

private fun createInitials(name: String): String {
    return name
        .trim()
        .split(" ")
        .filter { word ->
            word.isNotBlank()
        }
        .take(2)
        .joinToString("") { word ->
            word.firstOrNull()
                ?.uppercase()
                .orEmpty()
        }
        .ifBlank {
            "U"
        }
}