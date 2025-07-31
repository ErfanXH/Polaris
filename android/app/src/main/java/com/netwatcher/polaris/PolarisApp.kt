package com.netwatcher.polaris

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import androidx.work.Configuration

@HiltAndroidApp
class PolarisApp : Application(), Configuration.Provider {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build()
}