package com.netwatcher.polaris.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.work.*
import com.netwatcher.polaris.service.DataSyncWorker
import java.util.concurrent.TimeUnit

object DataSyncScheduler {
    private const val SYNC_WORK_TAG = "DataSyncWorker"
    const val PREFS_NAME = "SettingsPrefs"
    const val KEY_SYNC_INTERVAL = "SyncIntervalMinutes"

    fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun schedulePeriodicSync(context: Context) {
        val preferences = getPreferences(context)
        val interval = preferences.getLong(KEY_SYNC_INTERVAL, 30L)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Only run when connected to a network
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<DataSyncWorker>(
            interval, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SYNC_WORK_TAG,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, // Replace !! existing work with the new one
            syncRequest
        )
    }

    fun updateSyncInterval(context: Context, intervalMinutes: Long) {
        val preferences = getPreferences(context)
        with(preferences.edit()) {
            putLong(KEY_SYNC_INTERVAL, intervalMinutes)
            apply()
        }
        schedulePeriodicSync(context)
    }
}
