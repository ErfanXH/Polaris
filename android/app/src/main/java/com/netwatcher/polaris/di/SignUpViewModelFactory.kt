// src/main/java/com/netwatcher/polaris/SignUpViewModelFactory.kt (or within a di/util package)
package com.netwatcher.polaris.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.netwatcher.polaris.domain.usecase.SignUpUseCase
import com.netwatcher.polaris.presentation.sign_up.SignUpViewModel

/**
 * Factory for creating [SignUpViewModel] instances with injected dependencies.
 * Necessary when your ViewModel has constructor parameters and you're not using
 * a full-fledged dependency injection framework like Hilt.
 *
 * @param signUpUseCase The [SignUpUseCase] to be injected into the ViewModel.
 */
class SignUpViewModelFactory(
    private val signUpUseCase: SignUpUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignUpViewModel(signUpUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
