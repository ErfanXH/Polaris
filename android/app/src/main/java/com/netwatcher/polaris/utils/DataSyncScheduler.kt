package com.netwatcher.polaris.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.work.*
import com.netwatcher.polaris.service.DataSyncWorker
import java.util.concurrent.TimeUnit

/**
 * Utility object for scheduling the periodic data sync using WorkManager.
 */
object DataSyncScheduler {

    private const val SYNC_WORK_TAG = "DataSyncWorker"
    const val PREFS_NAME = "SettingsPrefs"
    const val KEY_SYNC_INTERVAL = "SyncIntervalMinutes"

    /**
     * Retrieves the shared preferences for app settings.
     */
    fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Schedules a periodic background sync task. If a task is already scheduled, it will be replaced.
     * The interval is read from SharedPreferences.
     */
    fun schedulePeriodicSync(context: Context) {
        val preferences = getPreferences(context)
        val interval = preferences.getLong(KEY_SYNC_INTERVAL, 30L) // Default to 30 minutes

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

    /**
     * Updates the sync interval in SharedPreferences and reschedules the sync worker.
     * @param context The application context.
     * @param intervalMinutes The new interval in minutes.
     */
    fun updateSyncInterval(context: Context, intervalMinutes: Long) {
        val preferences = getPreferences(context)
        with(preferences.edit()) {
            putLong(KEY_SYNC_INTERVAL, intervalMinutes)
            apply()
        }
        // Reschedule with the new interval
        schedulePeriodicSync(context)
    }
}
