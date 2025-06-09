package com.netwatcher.polaris.data.repository

import com.netwatcher.polaris.data.remote.AuthApi
import com.netwatcher.polaris.domain.model.LoginRequest
import com.netwatcher.polaris.domain.model.SignUpRequest
import com.netwatcher.polaris.domain.repository.AuthRepository
import android.util.Log

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
            Log.d("response", "$response")
            Log.d("request", "$request")
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
    override suspend fun login(request: LoginRequest): Result<Unit> {
        return try {
            val response = api.login(request) // ← This is the LOGIN call
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Login failed"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * A sealed class to represent the outcome of an operation.
 * This is a common pattern for handling network/data responses.
 */
/*
sealed class Result<out T> {
    object Loading : Result<Nothing>()
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
}
*/