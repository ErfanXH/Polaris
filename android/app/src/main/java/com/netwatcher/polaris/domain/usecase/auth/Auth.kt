package com.netwatcher.polaris.domain.usecase.auth

import com.netwatcher.polaris.domain.model.*
import com.netwatcher.polaris.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(request: SignUpRequest) = repository.signUp(request)
}

class LoginUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(request: LoginRequest) = repository.login(request)
}

class VerifyUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(request: VerificationRequest) = repository.verify(request)
}

class RetryVerificationUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(request: VerificationRetryRequest) =
        repository.retryVerification(request)
}

class SendResetCodeUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(numberOrEmail: String) = repository.sendResetCode(numberOrEmail)
}

class VerifyResetCodeUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(request: VerifyResetCodeRequest) =
        repository.verifyResetCode(request)
}

class ResetPasswordUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(request: ResetPasswordRequest) = repository.resetPassword(request)
}