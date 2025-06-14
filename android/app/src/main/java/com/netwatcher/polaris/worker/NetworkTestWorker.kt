package com.netwatcher.polaris.worker

import android.content.Context
import android.telephony.TelephonyManager
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.netwatcher.polaris.AppDatabaseHelper
import com.netwatcher.polaris.data.repository.NetworkRepositoryImpl
import com.netwatcher.polaris.di.NetworkModule
import java.util.concurrent.TimeUnit

class NetworkTestWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    private val database = AppDatabaseHelper.getDatabase(context)
    private val repository = NetworkRepositoryImpl(
        context = context,
        telephonyManager = telephonyManager,
        networkDataDao = database.networkDataDao(),
        api = NetworkModule.networkDataApi
    )

    override suspend fun doWork(): Result {
        return try {
            val result = repository.runNetworkTest()

            // Here you can save the result to a database or send it to a server
//            println("Background test result: $result")
            Log.d("Background", "$result")

//            repository.addNetworkData(result)

            Result.success()
        } catch (e: Exception) {
            Log.e("Background", "$e")
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "network_test_worker"

        fun schedule(context: Context) {
            Log.d("Worker", "Scheduled")

            val workRequest = PeriodicWorkRequestBuilder<NetworkTestWorker>(
                15,
                TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP, // Keep existing work if already scheduled
                workRequest
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}