package com.netwatcher.polaris.domain.repository

import com.netwatcher.polaris.domain.model.LoginRequest
import com.netwatcher.polaris.domain.model.SignUpRequest

/**
 * Interface defining the contract for authentication operations.
 * This belongs to the domain layer as it dictates what authentication services
 * the application needs, independent of how they are implemented (e.g., via network, local DB).
 */
interface AuthRepository {
    suspend fun signUp(request: SignUpRequest): Result<Unit>
    suspend fun login(request: LoginRequest): Result<Unit>
}