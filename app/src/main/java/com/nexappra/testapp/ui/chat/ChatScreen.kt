package com.nexappra.testapp.ui.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexappra.testapp.data.model.MessageKind
import com.nexappra.testapp.data.model.MessageUi
import com.nexappra.testapp.ui.components.AppAvatar
import com.nexappra.testapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    contactId: String,
    contactName: String,
    onBack: () -> Unit,
    onAudioCall: () -> Unit,
    onVideoCall: () -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var selectedMessage by remember {
        mutableStateOf<MessageUi?>(null)
    }

    Scaffold(
        containerColor = NexaBackground,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppAvatar(
                            initials = contactName
                                .split(" ")
                                .take(2)
                                .joinToString("") {
                                    it.firstOrNull()?.uppercase() ?: ""
                                },
                            online = true,
                            size = 38.dp
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = contactName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "Online",
                                style = MaterialTheme.typography.labelSmall,
                                color = NexaGreen
                            )
                        }
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
                            contentDescription = "More"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NexaSurface
                )
            )
        },
        bottomBar = {
            MessageComposer(
                onSend = viewModel::sendMessage
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                horizontal = 14.dp,
                vertical = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                SystemMessage(
                    text = "Messages are end-to-end encrypted"
                )
            }

            items(
                items = state.messages,
                key = { it.id }
            ) { message ->
                MessageBubble(
                    message = message,
                    onLongClick = {
                        selectedMessage = message
                    }
                )
            }
        }
    }

    selectedMessage?.let { message ->
        MessageActionSheet(
            message = message,
            onDismiss = {
                selectedMessage = null
            },
            onReact = {
                viewModel.addReaction(message.id, "❤️")
                selectedMessage = null
            },
            onStar = {
                viewModel.toggleStar(message.id)
                selectedMessage = null
            },
            onDelete = {
                viewModel.deleteMessage(message.id)
                selectedMessage = null
            }
        )
    }
}

@Composable
private fun SystemMessage(
    text: String
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFECEDEF)
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 6.dp
                ),
                style = MaterialTheme.typography.labelSmall,
                color = NexaSecondaryText
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MessageBubble(
    message: MessageUi,
    onLongClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.isMine) {
            Alignment.CenterEnd
        } else {
            Alignment.CenterStart
        }
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 310.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (message.isMine) 18.dp else 5.dp,
                        bottomEnd = if (message.isMine) 5.dp else 18.dp
                    )
                )
                .background(
                    if (message.isMine) {
                        NexaLightGreen
                    } else {
                        NexaSurface
                    }
                )
                .combinedClickable(
                    onClick = {},
                    onLongClick = onLongClick
                )
                .padding(10.dp)
        ) {
            when (message.kind) {
                MessageKind.TEXT -> {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                MessageKind.IMAGE -> {
                    Box(
                        modifier = Modifier
                            .width(250.dp)
                            .height(160.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        NexaDarkGreen,
                                        NexaGreen,
                                        Color(0xFF36C4A6)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Image,
                            contentDescription = null,
                            modifier = Modifier.size(52.dp),
                            tint = Color.White
                        )
                    }
                }

                MessageKind.FILE -> {
                    Row(
                        modifier = Modifier
                            .widthIn(min = 220.dp)
                            .background(
                                color = Color(0xFFF2F3F4),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = null,
                            tint = NexaGreen
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = message.fileName ?: "Document",
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = message.fileSize.orEmpty(),
                                style = MaterialTheme.typography.labelSmall,
                                color = NexaSecondaryText
                            )
                        }
                    }
                }

                MessageKind.VOICE -> {
                    Row(
                        modifier = Modifier.width(220.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = NexaGreen
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            thickness = 3.dp,
                            color = NexaGreen
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = message.duration ?: "0:00",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                message.reaction?.let {
                    Text(
                        text = it,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                }

                if (message.starred) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = NexaGreen
                    )
                }

                Text(
                    text = message.time,
                    style = MaterialTheme.typography.labelSmall,
                    color = NexaSecondaryText
                )

                if (message.isMine) {
                    Spacer(modifier = Modifier.width(3.dp))

                    Icon(
                        imageVector = Icons.Outlined.DoneAll,
                        contentDescription = "Read",
                        modifier = Modifier.size(15.dp),
                        tint = NexaGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageComposer(
    onSend: (String) -> Unit
) {
    var text by rememberSaveable {
        mutableStateOf("")
    }

    Surface(
        color = NexaSurface,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(
                    horizontal = 10.dp,
                    vertical = 8.dp
                ),
            verticalAlignment = Alignment.Bottom
        ) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Attachment"
                )
            }

            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("Message")
                },
                maxLines = 4,
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            FloatingActionButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onSend(text)
                        text = ""
                    }
                },
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                containerColor = NexaGreen
            ) {
                Icon(
                    imageVector = if (text.isBlank()) {
                        Icons.Outlined.Mic
                    } else {
                        Icons.Outlined.Send
                    },
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessageActionSheet(
    message: MessageUi,
    onDismiss: () -> Unit,
    onReact: () -> Unit,
    onStar: () -> Unit,
    onDelete: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = NexaSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf("👍", "❤️", "😂", "😮", "😢", "🙏")
                .forEach { reaction ->
                    TextButton(
                        onClick = onReact,
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        Text(
                            text = reaction,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
        }

        HorizontalDivider(color = NexaDivider)

        ActionRow(Icons.Outlined.Reply, "Reply", onDismiss)
        ActionRow(Icons.Outlined.AddReaction, "React", onReact)
        ActionRow(Icons.Outlined.ContentCopy, "Copy", onDismiss)
        ActionRow(Icons.Outlined.Forward, "Forward", onDismiss)

        if (message.isMine) {
            ActionRow(Icons.Outlined.Edit, "Edit", onDismiss)
        }

        ActionRow(Icons.Outlined.StarBorder, "Star", onStar)
        ActionRow(Icons.Outlined.SaveAlt, "Save media", onDismiss)
        ActionRow(Icons.Outlined.Download, "Download file", onDismiss)
        ActionRow(Icons.Outlined.DeleteOutline, "Delete for me", onDelete)

        if (message.isMine) {
            ActionRow(
                icon = Icons.Outlined.DeleteForever,
                label = "Delete for everyone",
                onClick = onDelete,
                danger = true
            )
        }

        ActionRow(Icons.Outlined.Info, "Message information", onDismiss)

        ActionRow(
            icon = Icons.Outlined.Flag,
            label = "Report",
            onClick = onDismiss,
            danger = true
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    danger: Boolean = false
) {
    ListItem(
        modifier = Modifier.fillMaxWidth(),
        headlineContent = {
            Text(
                text = label,
                color = if (danger) {
                    NexaDanger
                } else {
                    NexaPrimaryText
                }
            )
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (danger) {
                    NexaDanger
                } else {
                    NexaPrimaryText
                }
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = NexaSurface
        )
    )
}