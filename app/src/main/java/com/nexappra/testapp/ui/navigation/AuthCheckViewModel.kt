package com.nexappra.testapp.ui.navigation

import androidx.lifecycle.ViewModel
import com.nexappra.testapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthCheckViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _destinationRoute = MutableStateFlow<String?>(null)

    val destinationRoute: StateFlow<String?> =
        _destinationRoute.asStateFlow()

    init {
        checkUserSession()
    }

    private fun checkUserSession() {
        _destinationRoute.value =
            if (authRepository.currentUser != null) {
                Routes.Home.route
            } else {
                Routes.Login.route
            }
    }

    fun refreshSession() {
        checkUserSession()
    }
}