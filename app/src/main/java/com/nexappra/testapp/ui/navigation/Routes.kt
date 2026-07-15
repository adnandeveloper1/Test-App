package com.nexappra.testapp.ui.navigation

import android.net.Uri

sealed class Routes(
    val route: String
) {
    object AuthCheck : Routes("auth_check")
    object Login : Routes("login")
    object Register : Routes("register")
    object Home : Routes("home")
    object NewChat : Routes("new_chat")

    object Chat : Routes(
        "chat/{contactId}/{contactName}"
    ) {
        fun createRoute(
            contactId: String,
            contactName: String
        ): String {
            return "chat/" +
                    "${Uri.encode(contactId)}/" +
                    Uri.encode(contactName)
        }
    }
}