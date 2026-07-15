package com.nexappra.testapp.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexappra.testapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(
            currentStep = RegisterStep.PersonalInfo
        )
    )

    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { currentState ->
            currentState.copy(
                name = name,
                nameError = null,
                errorMessage = null
            )
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update { currentState ->
            currentState.copy(
                email = email,
                emailError = null,
                errorMessage = null
            )
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { currentState ->
            currentState.copy(
                password = password,
                passwordError = null,
                confirmPasswordError = null,
                errorMessage = null
            )
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { currentState ->
            currentState.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = null,
                errorMessage = null
            )
        }
    }

    fun goToSecureAccountStep() {
        if (_uiState.value.isLoading) {
            return
        }

        if (!validatePersonalInfo()) {
            return
        }

        _uiState.update { currentState ->
            currentState.copy(
                currentStep = RegisterStep.SecureAccount,
                errorMessage = null
            )
        }
    }

    fun goBackToPersonalInfo() {
        if (_uiState.value.isLoading) {
            return
        }

        _uiState.update { currentState ->
            currentState.copy(
                currentStep = RegisterStep.PersonalInfo,
                passwordError = null,
                confirmPasswordError = null,
                errorMessage = null
            )
        }
    }

    fun createAccount() {
        if (_uiState.value.isLoading) {
            return
        }

        if (!validateSecureAccount()) {
            return
        }

        val currentState = _uiState.value
        val name = currentState.name.trim()
        val email = currentState.email.trim()
        val password = currentState.password

        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )
            }

            authRepository.createAccount(
                name = _uiState.value.name.trim(),
                email = _uiState.value.email.trim(),
                password = _uiState.value.password
            ).fold(
                onSuccess = {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isSuccess = true,
                            successMessage = "Account created successfully.",
                            errorMessage = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isSuccess = false,
                            successMessage = null,
                            errorMessage = error.message
                                ?: "Unable to create an account right now."
                        )
                    }
                }
            )
        }
    }

    fun clearError() {
        _uiState.update { currentState ->
            currentState.copy(errorMessage = null)
        }
    }

    fun onNavigationHandled() {
        _uiState.update { currentState ->
            currentState.copy(
                isSuccess = false,
                successMessage = null
            )
        }
    }

    private fun validatePersonalInfo(): Boolean {
        val currentState = _uiState.value
        val name = currentState.name.trim()
        val email = currentState.email.trim()

        val nameError = when {
            name.isBlank() -> {
                "Full name is required."
            }

            name.length < 2 -> {
                "Full name must contain at least 2 characters."
            }

            else -> null
        }

        val emailError = when {
            email.isBlank() -> {
                "Email is required."
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                "Please enter a valid email address."
            }

            else -> null
        }

        _uiState.update { state ->
            state.copy(
                nameError = nameError,
                emailError = emailError,
                errorMessage = null
            )
        }

        return nameError == null && emailError == null
    }

    private fun validateSecureAccount(): Boolean {
        val personalInfoValid = validatePersonalInfo()

        val currentState = _uiState.value
        val password = currentState.password
        val confirmPassword = currentState.confirmPassword

        val passwordError = when {
            password.isBlank() -> {
                "Password is required."
            }

            password.length < 6 -> {
                "Password must contain at least 6 characters."
            }

            else -> null
        }

        val confirmPasswordError = when {
            confirmPassword.isBlank() -> {
                "Confirm password is required."
            }

            confirmPassword != password -> {
                "Passwords do not match."
            }

            else -> null
        }

        _uiState.update { state ->
            state.copy(
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                errorMessage = null
            )
        }

        return personalInfoValid &&
                passwordError == null &&
                confirmPasswordError == null
    }
}