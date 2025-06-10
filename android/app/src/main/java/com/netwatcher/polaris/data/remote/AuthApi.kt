package com.netwatcher.polaris.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import com.netwatcher.polaris.domain.model.SignUpRequest
import com.netwatcher.polaris.domain.model.LoginRequest
import com.netwatcher.polaris.domain.model.TokenResponse
import com.netwatcher.polaris.domain.model.VerificationRequest
import com.netwatcher.polaris.domain.model.VerificationRetryRequest

interface AuthApi {
    @POST("api/users/register/")
    suspend fun signUp(@Body request: SignUpRequest): Response<Unit>
    @POST("api/users/login/")
    suspend fun login(@Body request: LoginRequest): Response<TokenResponse>
    @POST("api/users/verification/")
    suspend fun verify(@Body request: VerificationRequest): Response<TokenResponse>
    @POST("api/users/get_verification_code/")
    suspend fun retryVerification(@Body request: VerificationRetryRequest): Response<Unit>
}