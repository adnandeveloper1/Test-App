package com.nexappra.testapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nexappra.testapp.ui.auth.LoadingGate
import com.nexappra.testapp.ui.auth.LoginScreen
import com.nexappra.testapp.ui.auth.RegisterScreen
import com.nexappra.testapp.ui.home.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.AuthCheck.route
    ) {
        composable(Routes.AuthCheck.route) {
            val viewModel: AuthCheckViewModel = hiltViewModel()
            val destinationRoute by viewModel.destinationRoute.collectAsStateWithLifecycle()

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
                    navController.navigate(Routes.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Routes.Home.route) {
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
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.Home.route) {
            HomeScreen(
                onLoggedOut = {
                    navController.navigate(Routes.Login.route) {
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
