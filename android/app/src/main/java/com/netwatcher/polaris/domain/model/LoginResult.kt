package com.netwatcher.polaris.domain.model

sealed class LoginResult {
    object Success : LoginResult()
    object RequiresVerification : LoginResult()
    data class Error(val message: String) : LoginResult()
}