package com.netwatcher.polaris.domain.repository

import com.netwatcher.polaris.domain.model.LoginResult
import com.netwatcher.polaris.domain.model.LoginRequest
import com.netwatcher.polaris.domain.model.SignUpRequest
import com.netwatcher.polaris.domain.model.VerificationRequest
import com.netwatcher.polaris.domain.model.VerificationRetryRequest

/**
 * Interface defining the contract for authentication operations.
 * This belongs to the domain layer as it dictates what authentication services
 * the application needs, independent of how they are implemented (e.g., via network, local DB).
 */
interface AuthRepository {
    suspend fun signUp(request: SignUpRequest): Result<Unit>
    suspend fun login(request: LoginRequest): LoginResult
    suspend fun verify(request: VerificationRequest): Result<Unit>
    suspend fun retryVerification(request: VerificationRetryRequest): Result<Unit>
}