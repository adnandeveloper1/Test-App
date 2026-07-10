package com.nexappra.testapp.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexappra.testapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update {
            it.copy(
                name = name,
                nameError = null,
                errorMessage = null
            )
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailError = null,
                errorMessage = null
            )
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordError = null,
                errorMessage = null
            )
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = null,
                errorMessage = null
            )
        }
    }

    fun goToSecureAccountStep() {
        if (!validatePersonalInfo()) {
            return
        }

        _uiState.update {
            it.copy(
                currentStep = RegisterStep.SecureAccount,
                errorMessage = null
            )
        }
    }

    fun goBackToPersonalInfo() {
        _uiState.update {
            it.copy(
                currentStep = RegisterStep.PersonalInfo,
                errorMessage = null
            )
        }
    }

    fun createAccount() {
        if (_uiState.value.isLoading || !validateSecureAccount()) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            authRepository.createAccount(
                name = _uiState.value.name.trim(),
                email = _uiState.value.email.trim(),
                password = _uiState.value.password
            ).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            successMessage = "Account created successfully."
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Unable to create an account right now."
                        )
                    }
                }
            )
        }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(isSuccess = false, successMessage = null) }
    }

    private fun validatePersonalInfo(): Boolean {
        val name = _uiState.value.name.trim()
        val email = _uiState.value.email.trim()

        val nameError = if (name.isBlank()) {
            "Full name is required."
        } else {
            null
        }

        val emailError = when {
            email.isBlank() -> "Email is required."
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Please enter a valid email address."
            else -> null
        }

        _uiState.update {
            it.copy(
                nameError = nameError,
                emailError = emailError,
                errorMessage = null
            )
        }

        return nameError == null && emailError == null
    }

    private fun validateSecureAccount(): Boolean {
        if (!validatePersonalInfo()) {
            return false
        }

        val password = _uiState.value.password
        val confirmPassword = _uiState.value.confirmPassword

        val passwordError = when {
            password.isBlank() -> "Password is required."
            password.length < 6 -> "Password must be at least 6 characters."
            else -> null
        }

        val confirmPasswordError = when {
            confirmPassword.isBlank() -> "Confirm password is required."
            confirmPassword != password -> "Passwords do not match."
            else -> null
        }

        _uiState.update {
            it.copy(
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                errorMessage = null
            )
        }

        return passwordError == null && confirmPasswordError == null
    }
}
