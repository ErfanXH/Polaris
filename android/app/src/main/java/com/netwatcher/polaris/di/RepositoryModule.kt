package com.netwatcher.polaris.di

import android.content.Context
import com.netwatcher.polaris.data.local.CookieManager
import com.netwatcher.polaris.data.remote.AuthApi
import com.netwatcher.polaris.data.remote.NetworkDataApi
import com.netwatcher.polaris.data.repository.AuthRepositoryImpl
import com.netwatcher.polaris.data.repository.NetworkRepositoryImpl
import com.netwatcher.polaris.domain.model.NetworkDataDatabase
import com.netwatcher.polaris.domain.repository.AuthRepository
import com.netwatcher.polaris.domain.repository.NetworkRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: AuthApi,
        cookieManager: CookieManager
    ): AuthRepository {
        return AuthRepositoryImpl(authApi, cookieManager)
    }

    @Provides
    @Singleton
    fun provideNetworkRepository(
        @ApplicationContext context: Context,
        api: NetworkDataApi,
        db: NetworkDataDatabase,
        cookieManager: CookieManager
    ): NetworkRepository {
        return NetworkRepositoryImpl(
            context = context,
            networkDataDao = db.networkDataDao(),
            api = api,
            cookieManager = cookieManager
        )
    }
}