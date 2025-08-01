package com.netwatcher.polaris.di

import android.app.Application
import com.netwatcher.polaris.data.local.CookieManager
import com.netwatcher.polaris.domain.repository.AuthRepository
import com.netwatcher.polaris.domain.repository.NetworkRepository
import com.netwatcher.polaris.domain.usecase.auth.AuthUseCases
import com.netwatcher.polaris.domain.usecase.auth.LoginUseCase
import com.netwatcher.polaris.domain.usecase.auth.ResetPasswordUseCase
import com.netwatcher.polaris.domain.usecase.auth.RetryVerificationUseCase
import com.netwatcher.polaris.domain.usecase.auth.SendResetCodeUseCase
import com.netwatcher.polaris.domain.usecase.auth.SignUpUseCase
import com.netwatcher.polaris.domain.usecase.auth.VerifyResetCodeUseCase
import com.netwatcher.polaris.domain.usecase.auth.VerifyUseCase
import com.netwatcher.polaris.domain.usecase.home.HomeUseCases
import com.netwatcher.polaris.domain.usecase.home.LoadInitialStateUseCase
import com.netwatcher.polaris.domain.usecase.home.LogoutUseCase
import com.netwatcher.polaris.domain.usecase.home.RunNetworkTestUseCase
import com.netwatcher.polaris.domain.usecase.home.SelectSimUseCase
import com.netwatcher.polaris.domain.usecase.permission.PermissionUseCase
import com.netwatcher.polaris.domain.usecase.settings.LoadSimCardsUseCase
import com.netwatcher.polaris.domain.usecase.settings.SettingsUseCases
import com.netwatcher.polaris.domain.usecase.settings.TestConfigUseCases
import com.netwatcher.polaris.domain.usecase.settings.UpdateSyncIntervalUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideAuthUseCases(repository: AuthRepository): AuthUseCases {
        return AuthUseCases(
            signUp = SignUpUseCase(repository),
            login = LoginUseCase(repository),
            verify = VerifyUseCase(repository),
            retryVerification = RetryVerificationUseCase(repository),
            sendResetCode = SendResetCodeUseCase(repository),
            verifyResetCode = VerifyResetCodeUseCase(repository),
            resetPassword = ResetPasswordUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideHomeUseCases(
        app: Application,
        repository: NetworkRepository,
        cookieManager: CookieManager
    ): HomeUseCases {
        return HomeUseCases(
            selectedSim = SelectSimUseCase(app),
            runNetworkTest = RunNetworkTestUseCase(repository),
            loadInitialState = LoadInitialStateUseCase(repository),
            logout = LogoutUseCase(cookieManager)
        )
    }

    @Provides
    @Singleton
    fun provideSettingsUseCases(
        app: Application,
        testConfigUseCases: TestConfigUseCases
    ): SettingsUseCases {
        return SettingsUseCases(
            testConfig = testConfigUseCases,
            updateSyncInterval = UpdateSyncIntervalUseCase(app),
            loadSimCards = LoadSimCardsUseCase(app),
        )
    }

    @Provides
    @Singleton
    fun providePermissionUseCase(): PermissionUseCase {
        return PermissionUseCase()
    }
}
