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
                errorMessage = null
            )
        }
    }

    fun onRememberPasswordChange(rememberPassword: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                rememberPassword = rememberPassword
            )
        }
    }

    fun login() {
        if (_uiState.value.isLoading) return

        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        val emailError = when {
            email.isBlank() -> "Email is required."
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Enter a valid email address."

            else -> null
        }

        val passwordError = when {
            password.isBlank() -> "Password is required."
            password.length < 6 ->
                "Password must contain at least 6 characters."

            else -> null
        }

        if (emailError != null || passwordError != null) {
            _uiState.update { currentState ->
                currentState.copy(
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            authRepository.login(
                email = email,
                password = password
            ).fold(
                onSuccess = {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            isSuccess = true,
                            successMessage = "Login successful."
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = error.message
                                ?: "Unable to log in."
                        )
                    }
                }
            )
        }
    }

    fun resetPassword() {
        if (_uiState.value.isLoading) return

        val email = _uiState.value.email.trim()

        if (
            email.isBlank() ||
            !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        ) {
            _uiState.update { currentState ->
                currentState.copy(
                    emailError = "Enter a valid email address first."
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            authRepository.sendPasswordResetEmail(email).fold(
                onSuccess = {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            passwordResetSuccess =
                                "Password reset email has been sent."
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = error.message
                                ?: "Unable to send password reset email."
                        )
                    }
                }
            )
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

    fun clearPasswordResetSuccess() {
        _uiState.update { currentState ->
            currentState.copy(
                passwordResetSuccess = null
            )
        }
    }
}