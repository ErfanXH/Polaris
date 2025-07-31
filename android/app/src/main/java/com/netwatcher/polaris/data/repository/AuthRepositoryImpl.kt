package com.netwatcher.polaris.data.repository

import com.netwatcher.polaris.data.remote.AuthApi
import com.netwatcher.polaris.domain.model.LoginRequest
import com.netwatcher.polaris.domain.model.SignUpRequest
import com.netwatcher.polaris.domain.repository.AuthRepository
import com.netwatcher.polaris.data.local.CookieManager
import com.netwatcher.polaris.domain.model.LoginResult
import com.netwatcher.polaris.domain.model.ResetPasswordRequest
import com.netwatcher.polaris.domain.model.SendResetCodeRequest
import com.netwatcher.polaris.domain.model.VerificationRequest
import com.netwatcher.polaris.domain.model.VerificationRetryRequest
import com.netwatcher.polaris.domain.model.VerifyResetCodeRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val cookieManager: CookieManager
) : AuthRepository {

    override suspend fun signUp(request: SignUpRequest): Result<Unit> {
        return try {
            val response = api.signUp(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(request: LoginRequest): LoginResult {
        return try {
            val response = api.login(request)
            when {
                response.isSuccessful -> {
                    val body = response.body()
                    if (body != null) {
                        cookieManager.saveToken(body.access)
                        cookieManager.saveEmail(body.email)
                        LoginResult.Success
                    } else {
                        LoginResult.Error("Empty response")
                    }
                }

                response.code() == 401 -> LoginResult.RequiresVerification
                else -> {
                    val errorMessage = response.errorBody()?.string() ?: "Login failed"
                    LoginResult.Error(errorMessage)
                }
            }
        } catch (e: Exception) {
            LoginResult.Error(e.localizedMessage ?: "Unexpected error occurred")
        }
    }

    override suspend fun verify(request: VerificationRequest): Result<Unit> {
        return try {
            val response = api.verify(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    cookieManager.saveToken(body.access)
                    cookieManager.saveEmail(body.email)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Empty response"))
                }
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Verification failed"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun retryVerification(request: VerificationRetryRequest): Result<Unit> {
        return try {
            val response = api.retryVerification(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Retry failed"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendResetCode(numberOrEmail: String): Result<Unit> {
        return try {
            val response = api.sendResetCode(SendResetCodeRequest(numberOrEmail))
            if (response.isSuccessful) Result.success(Unit)
            else {
                val errorMessage = response.errorBody()?.string() ?: "Send reset code failed"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyResetCode(request: VerifyResetCodeRequest): Result<Unit> {
        return try {
            val response = api.verifyResetCode(request)
            if (response.isSuccessful) Result.success(Unit)
            else {
                val errorMessage = response.errorBody()?.string() ?: "Verify reset code failed"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(request: ResetPasswordRequest): Result<Unit> {
        return try {
            val response = api.resetPassword(request)
            if (response.isSuccessful) Result.success(Unit)
            else {
                val errorMessage = response.errorBody()?.string() ?: "Reset password failed"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}