package com.nexappra.testapp.ui.auth

data class AuthUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val rememberPassword: Boolean = false,
    val currentStep: RegisterStep = RegisterStep.PersonalInfo,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val successMessage: String? = null,
    val passwordResetSuccess: String? = null,
    val errorMessage: String? = null,
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

enum class RegisterStep {
    PersonalInfo,
    SecureAccount
}
