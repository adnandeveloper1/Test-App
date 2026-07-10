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
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailError = null,
                errorMessage = null,
                passwordResetSuccess = null
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

    fun onRememberPasswordChange(rememberPassword: Boolean) {
        _uiState.update { it.copy(rememberPassword = rememberPassword) }
    }

    fun login() {
        if (_uiState.value.isLoading || !validateLoginFields()) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            authRepository.login(
                email = _uiState.value.email.trim(),
                password = _uiState.value.password
            ).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            successMessage = "Login successful."
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Unable to log in right now."
                        )
                    }
                }
            )
        }
    }

    fun resetPassword() {
        if (_uiState.value.isLoading || !validateEmailForReset()) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    passwordResetSuccess = null
                )
            }

            authRepository.resetPassword(_uiState.value.email.trim()).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            passwordResetSuccess = "A password reset email has been sent."
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Unable to send a reset email right now."
                        )
                    }
                }
            )
        }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(isSuccess = false, successMessage = null) }
    }

    fun clearPasswordResetSuccess() {
        _uiState.update { it.copy(passwordResetSuccess = null) }
    }

    private fun validateLoginFields(): Boolean {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password
        var emailError: String? = null
        var passwordError: String? = null

        if (email.isBlank()) {
            emailError = "Email is required."
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Please enter a valid email address."
        }

        if (password.isBlank()) {
            passwordError = "Password is required."
        }

        _uiState.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
                errorMessage = null
            )
        }

        return emailError == null && passwordError == null
    }

    private fun validateEmailForReset(): Boolean {
        val email = _uiState.value.email.trim()
        val emailError = when {
            email.isBlank() -> "Email is required."
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Please enter a valid email address."
            else -> null
        }

        _uiState.update {
            it.copy(
                emailError = emailError,
                errorMessage = null
            )
        }

        return emailError == null
    }
}
