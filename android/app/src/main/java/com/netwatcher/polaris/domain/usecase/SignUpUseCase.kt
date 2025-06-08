// src/main/java/com/netwatcher/polaris/domain/usecase/SignUpUseCase.kt
package com.netwatcher.polaris.domain.usecase

import com.netwatcher.polaris.domain.model.SignUpRequest
import com.netwatcher.polaris.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use Case for the Sign-Up operation.
 * This class orchestrates the business logic for signing up a user.
 * It's responsible for validating the input (if necessary, beyond basic UI validation)
 * and calling the [AuthRepository] to perform the actual sign-up.
 *
 * @param authRepository The repository interface to interact with authentication services.
 * This is an interface, adhering to dependency inversion.
 */
class SignUpUseCase(private val authRepository: AuthRepository) {

    /**
     * Executes the sign-up operation.
     * @param request The [SignUpRequest] containing the user's details.
     * @return A [Flow] emitting a [Result] indicating the outcome of the sign-up attempt.
     */
    operator fun invoke(request: SignUpRequest): Flow<Result<Unit>> {
        // You could add more complex domain-level validation here if needed,
        // separate from basic UI field validation.
        return authRepository.signUp(request)
    }
}
