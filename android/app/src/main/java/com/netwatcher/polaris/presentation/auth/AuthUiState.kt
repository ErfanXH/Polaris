package com.netwatcher.polaris.presentation.auth

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    object RequiresVerification : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
