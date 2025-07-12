package com.netwatcher.polaris.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netwatcher.polaris.domain.model.LoginRequest
import com.netwatcher.polaris.domain.model.LoginResult
import com.netwatcher.polaris.domain.model.NetworkData
import com.netwatcher.polaris.domain.model.ResetPasswordRequest
import com.netwatcher.polaris.domain.model.SignUpRequest
import com.netwatcher.polaris.domain.model.VerificationRequest
import com.netwatcher.polaris.domain.model.VerificationRetryRequest
import com.netwatcher.polaris.domain.model.VerifyResetCodeRequest
import com.netwatcher.polaris.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    private val _userInfo = MutableStateFlow<NetworkData?>(null)
    val authUiState: StateFlow<AuthUiState> = _authUiState

    fun signUp(request: SignUpRequest) {
        _authUiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.signUp(request)
            _authUiState.value = result.fold(
                onSuccess = { AuthUiState.Success },
                onFailure = { AuthUiState.Error(it.message ?: "Unknown Error") }
            )
        }
    }

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading

            when (val result = repository.login(request)) {
                is LoginResult.Success -> _authUiState.value = AuthUiState.Success
                is LoginResult.RequiresVerification -> _authUiState.value =
                    AuthUiState.RequiresVerification

                is LoginResult.Error -> _authUiState.value = AuthUiState.Error(result.message)
            }
        }
    }

    fun verify(request: VerificationRequest) {
        _authUiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.verify(request)
            _authUiState.value = result.fold(
                onSuccess = { AuthUiState.Success },
                onFailure = { AuthUiState.Error(it.message ?: "Verification failed") }
            )
        }
    }

    fun retryVerification(request: VerificationRetryRequest) {
        _authUiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.retryVerification(request)
            _authUiState.value = result.fold(
                onSuccess = { AuthUiState.Success },
                onFailure = { AuthUiState.Error(it.message ?: "Retry failed") }
            )
        }
    }

    fun sendResetCode(numberOrEmail: String) {
        _authUiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.sendResetCode(numberOrEmail)
            _authUiState.value = result.fold(
                onSuccess = { AuthUiState.CodeSent },
                onFailure = { AuthUiState.Error(it.message ?: "Failed to send reset code") }
            )
        }
    }

    fun verifyResetCode(request: VerifyResetCodeRequest) {
        _authUiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.verifyResetCode(request)
            _authUiState.value = result.fold(
                onSuccess = { AuthUiState.CodeVerified },
                onFailure = { AuthUiState.Error(it.message ?: "Invalid verification code") }
            )
        }
    }

    fun resetPassword(request: ResetPasswordRequest) {
        _authUiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.resetPassword(request)
            _authUiState.value = result.fold(
                onSuccess = { AuthUiState.Success },
                onFailure = { AuthUiState.Error(it.message ?: "Failed to reset password") }
            )
        }
    }

    fun resetState() {
        _authUiState.value = AuthUiState.Idle
    }
}
