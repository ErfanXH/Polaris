package com.netwatcher.polaris.di

import com.netwatcher.polaris.data.remote.AuthApi
import com.netwatcher.polaris.data.remote.NetworkDataApi
import com.netwatcher.polaris.data.repository.AuthRepositoryImpl
import com.netwatcher.polaris.domain.repository.AuthRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    // Logging interceptor
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Logs headers + body
    }

    // OkHttpClient with logging
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://194.62.43.37/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authApi)
    }

    val networkDataApi: NetworkDataApi by lazy {
        retrofit.create(NetworkDataApi::class.java)
    }
}
