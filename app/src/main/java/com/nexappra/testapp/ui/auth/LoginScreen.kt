package com.nexappra.testapp.ui.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexappra.testapp.R
import com.nexappra.testapp.ui.theme.MainTextColor
import com.nexappra.testapp.ui.theme.SuccessGreen

@Composable
fun LoginScreen(
    onOpenRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(
                context,
                uiState.successMessage ?: context.getString(R.string.login_successful),
                Toast.LENGTH_SHORT
            ).show()
            viewModel.onNavigationHandled()
            onLoginSuccess()
        }
    }

    LaunchedEffect(uiState.passwordResetSuccess) {
        uiState.passwordResetSuccess?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearPasswordResetSuccess()
        }
    }

    AuthPageLayout(
        title = stringResource(R.string.login_header_title),
        subtitle = stringResource(R.string.login_header_subtitle),
        isLoading = uiState.isLoading
    ) {
        FormHeading(
            title = stringResource(R.string.login_form_title),
            description = stringResource(R.string.login_form_description)
        )

        Spacer(modifier = Modifier.height(24.dp))

        AuthTextField(
            label = stringResource(R.string.email_address),
            placeholder = stringResource(R.string.email_placeholder),
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
            keyboardType = KeyboardType.Email,
            enabled = !uiState.isLoading,
            errorMessage = uiState.emailError
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            label = stringResource(R.string.password),
            placeholder = stringResource(R.string.password_placeholder),
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            keyboardType = KeyboardType.Password,
            isPassword = true,
            enabled = !uiState.isLoading,
            errorMessage = uiState.passwordError
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.clickable(enabled = !uiState.isLoading) {
                    viewModel.onRememberPasswordChange(!uiState.rememberPassword)
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.rememberPassword,
                    onCheckedChange = viewModel::onRememberPasswordChange,
                    enabled = !uiState.isLoading,
                    colors = CheckboxDefaults.colors(
                        checkedColor = SuccessGreen
                    )
                )

                Text(
                    text = stringResource(R.string.save_password),
                    fontSize = 12.sp,
                    color = MainTextColor
                )
            }

            TextButton(
                onClick = viewModel::resetPassword,
                enabled = !uiState.isLoading
            ) {
                Text(
                    text = stringResource(R.string.forgot_password),
                    fontSize = 12.sp,
                    color = MainTextColor
                )
            }
        }

        uiState.errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(10.dp))
            StatusMessage(
                message = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        PrimaryButton(
            text = stringResource(R.string.login_account),
            onClick = viewModel::login,
            enabled = !uiState.isLoading,
            isLoading = uiState.isLoading
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onOpenRegister,
            enabled = !uiState.isLoading
        ) {
            Text(
                text = stringResource(R.string.create_new_account),
                color = MainTextColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
