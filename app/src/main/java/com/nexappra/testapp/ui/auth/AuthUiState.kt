package com.nexappra.testapp.ui.auth

enum class RegisterStep {
    PersonalInfo,
    SecureAccount
}

data class AuthUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val rememberPassword: Boolean = false,

    val currentStep: RegisterStep = RegisterStep.PersonalInfo,

    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val errorMessage: String? = null,

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val successMessage: String? = null,
    val passwordResetSuccess: String? = null
)