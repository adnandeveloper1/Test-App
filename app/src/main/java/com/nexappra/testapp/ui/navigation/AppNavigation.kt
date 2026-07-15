package com.nexappra.testapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nexappra.testapp.ui.auth.LoadingGate
import com.nexappra.testapp.ui.auth.LoginScreen
import com.nexappra.testapp.ui.auth.RegisterScreen
import com.nexappra.testapp.ui.home.MainHomeScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.AuthCheck.route
    ) {

        composable(
            route = Routes.AuthCheck.route
        ) {
            val viewModel: AuthCheckViewModel = hiltViewModel()

            val destinationState =
                viewModel.destinationRoute.collectAsStateWithLifecycle()

            val destinationRoute: String? =
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

        composable(
            route = Routes.Login.route
        ) {
            LoginScreen(
                onOpenRegister = {
                    navController.navigate(
                        route = Routes.Register.route
                    ) {
                        launchSingleTop = true
                    }
                },
                onLoginSuccess = {
                    navController.navigate(
                        route = Routes.Home.route
                    ) {
                        popUpTo(Routes.Login.route) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Routes.Register.route
        ) {
            RegisterScreen(
                onBackToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(
                        route = Routes.Home.route
                    ) {
                        popUpTo(Routes.Register.route) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Routes.Home.route
        ) {
            MainHomeScreen(
                onOpenChat = { contactId, contactName ->
                    // Chat navigation will be connected later.
                },
                onNewChat = {
                    // New-chat navigation will be connected later.
                },
                onLoggedOut = {
                    navController.navigate(
                        route = Routes.Login.route
                    ) {
                        popUpTo(Routes.Home.route) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }
    }
}