package com.netwatcher.polaris.domain.repository

import com.netwatcher.polaris.domain.model.LoginResult
import com.netwatcher.polaris.domain.model.LoginRequest
import com.netwatcher.polaris.domain.model.ResetPasswordRequest
import com.netwatcher.polaris.domain.model.SendResetCodeRequest
import com.netwatcher.polaris.domain.model.SignUpRequest
import com.netwatcher.polaris.domain.model.VerificationRequest
import com.netwatcher.polaris.domain.model.VerificationRetryRequest
import com.netwatcher.polaris.domain.model.VerifyResetCodeRequest

interface AuthRepository {
    suspend fun signUp(request: SignUpRequest): Result<Unit>
    suspend fun login(request: LoginRequest): LoginResult
    suspend fun verify(request: VerificationRequest): Result<Unit>
    suspend fun retryVerification(request: VerificationRetryRequest): Result<Unit>
    suspend fun sendResetCode(numberOrEmail: String): Result<Unit>
    suspend fun verifyResetCode(request: VerifyResetCodeRequest): Result<Unit>
    suspend fun resetPassword(request: ResetPasswordRequest): Result<Unit>
}