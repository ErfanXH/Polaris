package com.netwatcher.polaris.di

import androidx.hilt.work.HiltWorkerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HiltWorkerFactoryModule {

    @Provides
    fun provideWorkerFactory(
        workerFactory: HiltWorkerFactory
    ): androidx.work.WorkerFactory = workerFactory
}
