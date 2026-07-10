package com.nexappra.testapp.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexappra.testapp.R
import com.nexappra.testapp.ui.theme.MainTextColor
import com.nexappra.testapp.ui.theme.SecondaryTextColor

@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(
                context,
                uiState.successMessage ?: context.getString(R.string.account_created),
                Toast.LENGTH_SHORT
            ).show()
            viewModel.onNavigationHandled()
            onRegisterSuccess()
        }
    }

    AuthPageLayout(
        title = stringResource(R.string.join_us),
        subtitle = stringResource(R.string.create_free_account),
        isLoading = uiState.isLoading
    ) {
        when (uiState.currentStep) {
            RegisterStep.PersonalInfo -> PersonalInfoContent(
                uiState = uiState,
                onNameChange = viewModel::onNameChange,
                onEmailChange = viewModel::onEmailChange,
                onContinue = viewModel::goToSecureAccountStep,
                onBackToLogin = onBackToLogin
            )

            RegisterStep.SecureAccount -> SecureAccountContent(
                uiState = uiState,
                onPasswordChange = viewModel::onPasswordChange,
                onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                onCreateAccount = viewModel::createAccount,
                onBackToPersonalInfo = viewModel::goBackToPersonalInfo,
                onBackToLogin = onBackToLogin
            )
        }
    }
}

@Composable
private fun PersonalInfoContent(
    uiState: AuthUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onContinue: () -> Unit,
    onBackToLogin: () -> Unit
) {
    FormHeading(
        title = stringResource(R.string.personal_info),
        description = stringResource(R.string.personal_info_description)
    )

    Spacer(modifier = Modifier.height(24.dp))

    AuthTextField(
        label = stringResource(R.string.full_name),
        placeholder = stringResource(R.string.full_name_placeholder),
        value = uiState.name,
        onValueChange = onNameChange,
        enabled = !uiState.isLoading,
        errorMessage = uiState.nameError
    )

    Spacer(modifier = Modifier.height(16.dp))

    AuthTextField(
        label = stringResource(R.string.email_address),
        placeholder = stringResource(R.string.email_placeholder),
        value = uiState.email,
        onValueChange = onEmailChange,
        keyboardType = KeyboardType.Email,
        enabled = !uiState.isLoading,
        errorMessage = uiState.emailError
    )

    uiState.errorMessage?.let { message ->
        Spacer(modifier = Modifier.height(12.dp))
        StatusMessage(
            message = message,
            color = MaterialTheme.colorScheme.error
        )
    }

    Spacer(modifier = Modifier.height(22.dp))

    PrimaryButton(
        text = stringResource(R.string.save_continue),
        onClick = onContinue,
        enabled = !uiState.isLoading
    )

    Spacer(modifier = Modifier.height(8.dp))

    TextButton(
        onClick = onBackToLogin,
        enabled = !uiState.isLoading
    ) {
        Text(
            text = stringResource(R.string.back_to_login),
            color = MainTextColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
    }

    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
private fun SecureAccountContent(
    uiState: AuthUiState,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onCreateAccount: () -> Unit,
    onBackToPersonalInfo: () -> Unit,
    onBackToLogin: () -> Unit
) {
    FormHeading(
        title = stringResource(R.string.secure_account),
        description = stringResource(R.string.secure_account_description)
    )

    Spacer(modifier = Modifier.height(18.dp))

    Text(
        text = stringResource(R.string.secure_account_helper, uiState.email),
        modifier = Modifier.fillMaxWidth(),
        color = SecondaryTextColor,
        fontSize = 12.sp
    )

    Spacer(modifier = Modifier.height(16.dp))

    AuthTextField(
        label = stringResource(R.string.password),
        placeholder = stringResource(R.string.password_placeholder),
        value = uiState.password,
        onValueChange = onPasswordChange,
        keyboardType = KeyboardType.Password,
        isPassword = true,
        enabled = !uiState.isLoading,
        errorMessage = uiState.passwordError
    )

    Spacer(modifier = Modifier.height(16.dp))

    AuthTextField(
        label = stringResource(R.string.confirm_password),
        placeholder = stringResource(R.string.confirm_password_placeholder),
        value = uiState.confirmPassword,
        onValueChange = onConfirmPasswordChange,
        keyboardType = KeyboardType.Password,
        isPassword = true,
        enabled = !uiState.isLoading,
        errorMessage = uiState.confirmPasswordError
    )

    uiState.errorMessage?.let { message ->
        Spacer(modifier = Modifier.height(12.dp))
        StatusMessage(
            message = message,
            color = MaterialTheme.colorScheme.error
        )
    }

    Spacer(modifier = Modifier.height(22.dp))

    PrimaryButton(
        text = stringResource(R.string.create_account),
        onClick = onCreateAccount,
        enabled = !uiState.isLoading,
        isLoading = uiState.isLoading
    )

    Spacer(modifier = Modifier.height(6.dp))

    TextButton(
        onClick = onBackToPersonalInfo,
        enabled = !uiState.isLoading
    ) {
        Text(
            text = stringResource(R.string.back_to_personal_info),
            color = MainTextColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
    }

    TextButton(
        onClick = onBackToLogin,
        enabled = !uiState.isLoading
    ) {
        Text(
            text = stringResource(R.string.back_to_login),
            color = SecondaryTextColor,
            fontSize = 12.sp
        )
    }

    Spacer(modifier = Modifier.height(20.dp))
}
