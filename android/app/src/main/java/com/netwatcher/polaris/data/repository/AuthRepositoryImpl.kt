package com.netwatcher.polaris.data.repository

import com.netwatcher.polaris.data.remote.AuthApi
import com.netwatcher.polaris.domain.model.LoginRequest
import com.netwatcher.polaris.domain.model.SignUpRequest
import com.netwatcher.polaris.domain.repository.AuthRepository
import android.util.Log
import com.netwatcher.polaris.di.TokenManager
import com.netwatcher.polaris.domain.model.LoginResult
import com.netwatcher.polaris.domain.model.VerificationRequest
import com.netwatcher.polaris.domain.model.VerificationRetryRequest

/**
 * Concrete implementation of the [AuthRepository] interface.
 * This class handles the actual data operations for authentication,
 * such as making network requests or interacting with a local database.
 * For this example, it simulates a network call.
 */
class AuthRepositoryImpl(
    private val api: AuthApi
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
            val response = api.login(request) // ← This is the LOGIN call
            when {
                response.isSuccessful -> {
                    val body = response.body()
                    if (body != null) {
                        TokenManager.saveToken(body.access)
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
                    TokenManager.saveToken(body.access)
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
}