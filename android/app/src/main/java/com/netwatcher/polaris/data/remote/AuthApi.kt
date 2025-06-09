package com.netwatcher.polaris.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import com.netwatcher.polaris.domain.model.SignUpRequest
import com.netwatcher.polaris.domain.model.LoginRequest

interface AuthApi {
    @POST("api/users/register/")
    suspend fun signUp(@Body request: SignUpRequest): Response<Unit>
    @POST("api/users/login/")
    suspend fun login(@Body request: LoginRequest): Response<Unit>
}