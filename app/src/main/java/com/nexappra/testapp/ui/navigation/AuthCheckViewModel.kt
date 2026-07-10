package com.nexappra.testapp.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexappra.testapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthCheckViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _destinationRoute = MutableStateFlow<String?>(null)
    val destinationRoute: StateFlow<String?> = _destinationRoute.asStateFlow()

    init {
        viewModelScope.launch {
            _destinationRoute.value = if (authRepository.isUserLoggedIn()) {
                Routes.Home.route
            } else {
                Routes.Login.route
            }
        }
    }
}
