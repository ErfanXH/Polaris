// src/main/java/com/netwatcher/polaris/signup/SignUpViewModel.kt
package com.netwatcher.polaris.presentation.sign_up

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netwatcher.polaris.data.repository.Result // Import the Result sealed class
import com.netwatcher.polaris.domain.model.SignUpRequest
import com.netwatcher.polaris.domain.usecase.SignUpUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Sign-Up screen.
 * This class handles the UI logic, holds the [SignUpState], and processes user interactions.
 * It now uses a [SignUpUseCase] from the domain layer to perform the actual sign-up operation.
 *
 * @param signUpUseCase The Use Case responsible for executing the sign-up business logic.
 * This dependency should be provided via dependency injection (e.g., Hilt).
 */
class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase // Injected dependency
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpState())
    val uiState: StateFlow<SignUpState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, errorMessage = null) }
    }

    fun onPhoneNumberChange(newPhoneNumber: String) {
        _uiState.update { it.copy(phoneNumber = newPhoneNumber, errorMessage = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null) }
    }

    /**
     * Handles the sign-up button click event.
     * This function now calls the [SignUpUseCase] to perform the sign-up and
     * observes its [Result] to update the UI state.
     */
    fun onSignUpClick() {
        // Clear any previous error messages before starting a new operation
        _uiState.update { it.copy(isLoading = true, errorMessage = null, isSignUpSuccessful = false) }

        // Basic UI validation before calling the use case
        if (_uiState.value.email.isBlank() || _uiState.value.password.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Email and password cannot be empty.",
                    isSignUpSuccessful = false
                )
            }
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_uiState.value.email).matches()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Please enter a valid email address.",
                    isSignUpSuccessful = false
                )
            }
            return
        }
        if (_uiState.value.password.length < 6) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Password must be at least 6 characters long.",
                    isSignUpSuccessful = false
                )
            }
            return
        }
        if (_uiState.value.phoneNumber.isNotBlank() && !android.util.Patterns.PHONE.matcher(_uiState.value.phoneNumber).matches()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Please enter a valid phone number, or leave it blank.",
                    isSignUpSuccessful = false
                )
            }
            return
        }

        // Create the SignUpRequest from the current UI state
        val signUpRequest = SignUpRequest(
            email = uiState.value.email,
            phoneNumber = uiState.value.phoneNumber.ifBlank { null }, // Pass null if empty
            password = uiState.value.password
        )

        viewModelScope.launch {
            signUpUseCase(signUpRequest).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Not strictly needed here as we set isLoading=true above,
                        // but good for completeness if use case emits intermediate loading states
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSignUpSuccessful = true,
                                errorMessage = null // Clear error on success
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.exception.message ?: "An unknown error occurred.",
                                isSignUpSuccessful = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun signUpComplete() {
        _uiState.update { it.copy(isSignUpSuccessful = false) }
    }
}
