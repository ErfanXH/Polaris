// src/main/java/com/netwatcher/polaris/signup/SignUpState.kt
package com.netwatcher.polaris.presentation.sign_up

/**
 * Represents the current UI state of the Sign-Up screen.
 * This data class holds all mutable data that the UI needs to display,
 * including user input, loading indicators, and messages.
 *
 * @property email The current text in the email input field.
 * @property phoneNumber The current text in the phone number input field.
 * @property password The current text in the password input field.
 * @property isLoading A boolean indicating if a sign-up operation is in progress (e.g., showing a progress bar).
 * @property errorMessage A nullable string to display an error message to the user.
 * @property isSignUpSuccessful A boolean indicating if the sign-up operation completed successfully.
 */
data class SignUpState(
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSignUpSuccessful: Boolean = false
)
