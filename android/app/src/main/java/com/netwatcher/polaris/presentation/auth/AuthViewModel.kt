package com.netwatcher.polaris.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netwatcher.polaris.data.local.CookieManager
import com.netwatcher.polaris.domain.model.LoginRequest
import com.netwatcher.polaris.domain.model.LoginResult
import com.netwatcher.polaris.domain.model.NetworkData
import com.netwatcher.polaris.domain.model.ResetPasswordRequest
import com.netwatcher.polaris.domain.model.SignUpRequest
import com.netwatcher.polaris.domain.model.VerificationRequest
import com.netwatcher.polaris.domain.model.VerificationRetryRequest
import com.netwatcher.polaris.domain.model.VerifyResetCodeRequest
import com.netwatcher.polaris.domain.repository.AuthRepository
import com.netwatcher.polaris.domain.usecase.auth.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val cookieManager: CookieManager
) : ViewModel() {

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authUiState: StateFlow<AuthUiState> = _authUiState


    fun validateSignUpInputs(
        email: String,
        phoneNumber: String,
        password: String
    ): Pair<Boolean, Map<String, String?>> {
        var isValid = true
        val errors = mutableMapOf<String, String?>()

        if (email.isBlank()) {
            isValid = false
            errors["email"] = "Email is required"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isValid = false
            errors["email"] = "Invalid email format"
        }

        if (phoneNumber.isBlank()) {
            isValid = false
            errors["phoneNumber"] = "Phone number is required"
        } else if (!phoneNumber.startsWith("09")) {
            isValid = false
            errors["phoneNumber"] = "Phone number should start with '09'"
        } else if (phoneNumber.length != 11) {
            isValid = false
            errors["phoneNumber"] = "Phone number should be 11 digits"
        }

        if (password.isBlank()) {
            isValid = false
            errors["password"] = "Password is required"
        } else if (password.length < 6) {
            isValid = false
            errors["password"] = "Password must be at least 6 characters"
        }

        return isValid to errors
    }

    fun signUp(email: String, phoneNumber: String, password: String) {
        _authUiState.value = AuthUiState.Loading
        val request = SignUpRequest(email, phoneNumber, password)
        viewModelScope.launch {
            val result = authUseCases.signUp(request)
            _authUiState.value = result.fold(
                onSuccess = { AuthUiState.Success },
                onFailure = { AuthUiState.Error(it.message ?: "Unknown Error") }
            )
        }
    }

    fun validateLoginInputs(
        numberOrEmail: String,
        password: String
    ): Pair<Boolean, Map<String, String?>> {
        var isValid = true
        val errors = mutableMapOf<String, String?>()

        if (numberOrEmail.isBlank()) {
            errors["numberOrEmail"] = "Email or phone is required"
            isValid = false
        }

        if (password.isBlank()) {
            errors["password"] = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            errors["password"] = "Password must be at least 6 characters"
            isValid = false
        }

        return isValid to errors
    }

    fun login(request: LoginRequest) {
        _authUiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = authUseCases.login(request)) {
                is LoginResult.Success -> _authUiState.value = AuthUiState.Success
                is LoginResult.RequiresVerification -> _authUiState.value =
                    AuthUiState.RequiresVerification

                is LoginResult.Error -> _authUiState.value = AuthUiState.Error(result.message)
            }
        }
    }

    fun verify(numberOrEmail: String, code: String, password: String) {
        _authUiState.value = AuthUiState.Loading
        val request = VerificationRequest(numberOrEmail, password, code)
        viewModelScope.launch {
            val result = authUseCases.verify(request)
            _authUiState.value = result.fold(
                onSuccess = { AuthUiState.Success },
                onFailure = { AuthUiState.Error(it.message ?: "Verification failed") }
            )
        }
    }

    fun retryVerification(request: VerificationRetryRequest) {
        _authUiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = authUseCases.retryVerification(request)
            _authUiState.value = result.fold(
                onSuccess = { AuthUiState.Success },
                onFailure = { AuthUiState.Error(it.message ?: "Retry failed") }
            )
        }
    }

    fun validateIdentifier(identifier: String): String? {
        return if (identifier.isBlank()) "Field can't be empty" else null
    }

    fun validateVerificationCode(code: String): String? {
        return if (code.length != 5 || !code.all { it.isDigit() }) "Invalid code" else null
    }

    fun validatePasswords(
        newPassword: String,
        confirmPassword: String
    ): Pair<Boolean, Map<String, String?>> {
        var isValid = true
        val errors = mutableMapOf<String, String?>()


        if (newPassword.length < 6) {
            isValid = false
            errors["newPassword"] = "Password too short"
        }
        if (newPassword != confirmPassword) {
            isValid = false
            errors["confirmPassword"] = "Passwords don't match"
        }
        return isValid to errors
    }

    fun sendResetCode(numberOrEmail: String) {
        _authUiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = authUseCases.sendResetCode(numberOrEmail)
            _authUiState.value = result.fold(
                onSuccess = { AuthUiState.CodeSent },
                onFailure = { AuthUiState.Error(it.message ?: "Failed to send reset code") }
            )
        }
    }

    fun verifyResetCode(numberOrEmail: String, code: String) {
        _authUiState.value = AuthUiState.Loading
        val request = VerifyResetCodeRequest(numberOrEmail, code)
        viewModelScope.launch {
            val result = authUseCases.verifyResetCode(request)
            _authUiState.value = result.fold(
                onSuccess = { AuthUiState.CodeVerified },
                onFailure = { AuthUiState.Error(it.message ?: "Invalid verification code") }
            )
        }
    }

    fun resetPassword(numberOrEmail: String, code: String, password: String) {
        _authUiState.value = AuthUiState.Loading
        val request = ResetPasswordRequest(numberOrEmail, code, password)
        viewModelScope.launch {
            val result = authUseCases.resetPassword(request)
            _authUiState.value = result.fold(
                onSuccess = { AuthUiState.Success },
                onFailure = { AuthUiState.Error(it.message ?: "Failed to reset password") }
            )
        }
    }

    suspend fun isUserLoggedIn(): Boolean {
        val token = cookieManager.getToken().firstOrNull()
        return !token.isNullOrEmpty()
    }

    fun resetState() {
        _authUiState.value = AuthUiState.Idle
    }
}
