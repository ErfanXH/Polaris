package com.netwatcher.polaris.domain.usecase

import com.netwatcher.polaris.domain.model.SignUpRequest
import com.netwatcher.polaris.domain.repository.AuthRepository

class SignUpUseCase(private val authRepository: AuthRepository) {

    /**
     * Executes the sign-up operation.
     * @param request The [SignUpRequest] containing the user's details.
     */
    suspend fun invoke(request: SignUpRequest): Result<Unit> {
        // You could add more complex domain-level validation here if needed,
        // separate from basic UI field validation.
        return authRepository.signUp(request)
    }
}
