package com.nexappra.testapp.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nexappra.testapp.ui.auth.LoadingGate
import com.nexappra.testapp.ui.auth.LoginScreen
import com.nexappra.testapp.ui.auth.RegisterScreen
import com.nexappra.testapp.ui.chat.ChatScreen
import com.nexappra.testapp.ui.home.MainHomeScreen
import com.nexappra.testapp.ui.newchat.NewChatScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.AuthCheck.route
    ) {
        composable(Routes.AuthCheck.route) {
            val viewModel: AuthCheckViewModel =
                hiltViewModel()

            val destinationState =
                viewModel.destinationRoute
                    .collectAsStateWithLifecycle()

            val destinationRoute =
                destinationState.value

            LaunchedEffect(destinationRoute) {
                destinationRoute?.let { route ->
                    navController.navigate(route) {
                        popUpTo(Routes.AuthCheck.route) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            }

            LoadingGate()
        }

        composable(Routes.Login.route) {
            LoginScreen(
                onOpenRegister = {
                    navController.navigate(
                        Routes.Register.route
                    )
                },
                onLoginSuccess = {
                    navController.navigate(
                        Routes.Home.route
                    ) {
                        popUpTo(Routes.Login.route) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.Register.route) {
            RegisterScreen(
                onBackToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(
                        Routes.Home.route
                    ) {
                        popUpTo(Routes.Register.route) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.Home.route) {
            MainHomeScreen(
                onOpenChat = { contactId, contactName ->
                    navController.navigate(
                        Routes.Chat.createRoute(
                            contactId = contactId,
                            contactName = contactName
                        )
                    )
                },
                onNewChat = {
                    navController.navigate(
                        Routes.NewChat.route
                    )
                },
                onLoggedOut = {
                    navController.navigate(
                        Routes.Login.route
                    ) {
                        popUpTo(Routes.Home.route) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.NewChat.route) {
            NewChatScreen(
                onBack = {
                    navController.popBackStack()
                },
                onContactSelected = {
                        contactId,
                        contactName ->

                    navController.navigate(
                        Routes.Chat.createRoute(
                            contactId = contactId,
                            contactName = contactName
                        )
                    )
                }
            )
        }

        composable(
            route = Routes.Chat.route,
            arguments = listOf(
                navArgument("contactId") {
                    type = NavType.StringType
                },
                navArgument("contactName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val contactId = Uri.decode(
                backStackEntry.arguments
                    ?.getString("contactId")
                    .orEmpty()
            )

            val contactName = Uri.decode(
                backStackEntry.arguments
                    ?.getString("contactName")
                    .orEmpty()
            )

            ChatScreen(
                contactId = contactId,
                contactName = contactName,
                onBack = {
                    navController.popBackStack()
                },
                onAudioCall = {
                    // Audio call screen will be connected here.
                },
                onVideoCall = {
                    // Video call screen will be connected here.
                }
            )
        }
    }
}