package com.netwatcher.polaris.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netwatcher.polaris.domain.model.LoginRequest
import com.netwatcher.polaris.domain.model.SignUpRequest
import com.netwatcher.polaris.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
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
        _authUiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.login(request)
            _authUiState.value = result.fold(
                onSuccess = { AuthUiState.Success },
                onFailure = { AuthUiState.Error(it.message ?: "Unknown Error") }
            )
        }
    }
}
