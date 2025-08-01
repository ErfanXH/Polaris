package com.netwatcher.polaris.domain.usecase.auth

import javax.inject.Inject

data class AuthUseCases @Inject constructor(
    val signUp: SignUpUseCase,
    val login: LoginUseCase,
    val verify: VerifyUseCase,
    val retryVerification: RetryVerificationUseCase,
    val sendResetCode: SendResetCodeUseCase,
    val verifyResetCode: VerifyResetCodeUseCase,
    val resetPassword: ResetPasswordUseCase
)
