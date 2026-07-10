package com.nexappra.testapp.ui.home

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexappra.testapp.R

@Composable
fun NotificationTestSection(
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel =
        hiltViewModel()
) {
    val uiState by viewModel.uiState
        .collectAsStateWithLifecycle()

    val context = LocalContext.current

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            viewModel.onNotificationPermissionResult(
                isGranted
            )
        }

    LaunchedEffect(Unit) {
        val permissionGranted =
            Build.VERSION.SDK_INT <
                    Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

        viewModel.onNotificationPermissionResult(
            permissionGranted
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(
                    R.string.notification_test_title
                ),
                style =
                    MaterialTheme.typography.titleLarge
            )

            Text(
                text = stringResource(
                    if (
                        uiState.notificationPermissionGranted
                    ) {
                        R.string.notification_permission_allowed
                    } else {
                        R.string.notification_permission_not_allowed
                    }
                )
            )

            if (
                !uiState.notificationPermissionGranted &&
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.TIRAMISU
            ) {
                Button(
                    onClick = {
                        permissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    }
                ) {
                    Text(
                        text = stringResource(
                            R.string.enable_notifications
                        )
                    )
                }
            }

            Text(
                text = stringResource(
                    R.string.fcm_token_title
                ),
                style =
                    MaterialTheme.typography.titleMedium
            )

            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }

                uiState.errorMessage != null -> {
                    Text(
                        text = stringResource(
                            R.string.fcm_token_error
                        ),
                        color =
                            MaterialTheme.colorScheme.error
                    )
                }

                uiState.fcmToken.isNotBlank() -> {
                    SelectionContainer {
                        Text(
                            text = uiState.fcmToken,
                            fontSize = 11.sp
                        )
                    }
                }

                else -> {
                    Text(
                        text = stringResource(
                            R.string.loading_fcm_token
                        )
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    enabled =
                        uiState.fcmToken.isNotBlank(),
                    onClick = {
                        copyToken(
                            context = context,
                            token = uiState.fcmToken
                        )
                    }
                ) {
                    Text(
                        text = stringResource(
                            R.string.copy_token
                        )
                    )
                }

                OutlinedButton(
                    enabled = !uiState.isLoading,
                    onClick = viewModel::loadFcmToken
                ) {
                    Text(
                        text = stringResource(
                            R.string.refresh_token
                        )
                    )
                }
            }
        }
    }
}

private fun copyToken(
    context: Context,
    token: String
) {
    val clipboardManager =
        context.getSystemService(
            Context.CLIPBOARD_SERVICE
        ) as ClipboardManager

    val clip = ClipData.newPlainText(
        "FCM token",
        token
    )

    clipboardManager.setPrimaryClip(clip)

    Toast.makeText(
        context,
        context.getString(R.string.token_copied),
        Toast.LENGTH_SHORT
    ).show()
}