// src/main/java/com/netwatcher/polaris/domain/repository/AuthRepository.kt
package com.netwatcher.polaris.domain.repository

import com.netwatcher.polaris.domain.model.SignUpRequest
import kotlinx.coroutines.flow.Flow
import com.netwatcher.polaris.data.repository.Result as DataResult // Import your custom Result with an alias

/**
 * Interface defining the contract for authentication operations.
 * This belongs to the domain layer as it dictates what authentication services
 * the application needs, independent of how they are implemented (e.g., via network, local DB).
 */
interface AuthRepository {
    /**
     * Attempts to sign up a new user.
     * @param request The [SignUpRequest] containing user credentials.
     * @return A [Flow] emitting a [DataResult] indicating success or failure.
     * On success, the [DataResult.Success] can contain a Unit or a User object.
     * On failure, the [DataResult.Error] will contain an an exception.
     */
    fun signUp(request: SignUpRequest): Flow<DataResult<Unit>> // Use the aliased custom Result
}
