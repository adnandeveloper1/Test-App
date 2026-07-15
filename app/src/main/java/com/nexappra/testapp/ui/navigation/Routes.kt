package com.nexappra.testapp.ui.navigation

sealed class Routes(
    val route: String
) {
    data object AuthCheck : Routes("auth_check")
    data object Login : Routes("login")
    data object Register : Routes("register")
    data object Home : Routes("home")
    data object NewChat : Routes("new_chat")
}