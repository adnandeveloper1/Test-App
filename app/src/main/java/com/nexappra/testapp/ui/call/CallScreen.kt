package com.nexappra.testapp.ui.call

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CallEnd
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MicOff
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.outlined.VideocamOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexappra.testapp.ui.components.AppAvatar
import com.nexappra.testapp.ui.theme.NexaBackground
import com.nexappra.testapp.ui.theme.NexaDanger
import com.nexappra.testapp.ui.theme.NexaGreen

@Composable
fun CallScreen(
    contactId: String,
    contactName: String,
    isVideoCall: Boolean,
    onEndCall: () -> Unit
) {
    var isMuted by remember {
        mutableStateOf(false)
    }

    var isVideoEnabled by remember {
        mutableStateOf(isVideoCall)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NexaBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isVideoCall) {
                        "Video call"
                    } else {
                        "Audio call"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.size(16.dp))

                AppAvatar(
                    initials = contactName
                        .split(" ")
                        .filter { it.isNotBlank() }
                        .take(2)
                        .joinToString("") {
                            it.firstOrNull()
                                ?.uppercase()
                                .orEmpty()
                        },
                    online = true,
                    size = 100.dp
                )

                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    text = contactName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Calling…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White
                ) {
                    IconButton(
                        onClick = {
                            isMuted = !isMuted
                        },
                        modifier = Modifier.size(58.dp)
                    ) {
                        Icon(
                            imageVector = if (isMuted) {
                                Icons.Outlined.MicOff
                            } else {
                                Icons.Outlined.Mic
                            },
                            contentDescription = if (isMuted) {
                                "Unmute"
                            } else {
                                "Mute"
                            }
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = NexaDanger
                ) {
                    IconButton(
                        onClick = onEndCall,
                        modifier = Modifier.size(68.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CallEnd,
                            contentDescription = "End call",
                            tint = Color.White
                        )
                    }
                }

                if (isVideoCall) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White
                    ) {
                        IconButton(
                            onClick = {
                                isVideoEnabled = !isVideoEnabled
                            },
                            modifier = Modifier.size(58.dp)
                        ) {
                            Icon(
                                imageVector = if (isVideoEnabled) {
                                    Icons.Outlined.Videocam
                                } else {
                                    Icons.Outlined.VideocamOff
                                },
                                contentDescription = "Toggle video",
                                tint = NexaGreen
                            )
                        }
                    }
                }
            }
        }
    }
}