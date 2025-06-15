//package com.netwatcher.polaris.worker
//
//import android.content.Context
//import androidx.work.CoroutineWorker
//import androidx.work.ExistingPeriodicWorkPolicy
//import androidx.work.PeriodicWorkRequestBuilder
//import androidx.work.WorkManager
//import androidx.work.WorkerParameters
//import com.netwatcher.polaris.AppDatabaseHelper
//import com.netwatcher.polaris.di.NetworkModule
//import com.netwatcher.polaris.di.TokenManager
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.firstOrNull
//import kotlinx.coroutines.withContext
//import java.util.concurrent.TimeUnit
//
//class SyncWorker(
//    context: Context,
//    workerParams: WorkerParameters
//) : CoroutineWorker(context, workerParams) {
//
//    private val database = AppDatabaseHelper.getDatabase(context)
//    private val networkDao = database.networkDataDao()
//    private val apiService = NetworkModule.networkDataApi
//
//    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
//        try {
//            val token = TokenManager.getToken().firstOrNull() ?: return@withContext Result.retry()
//
//            val unsyncedData = networkDao.getUnsyncedData()
//            if (unsyncedData.isEmpty()) return@withContext Result.success()
//
//            // Try to upload in batches
//            val batchSize = 20
//            unsyncedData.chunked(batchSize).forEach { batch ->
//                try {
//                    val response = apiService.uploadNetworkDataBatch(
//                        token = token,
//                        data = batch
//                    )
//
//                    if (response.isSuccessful) {
//                        networkDao.markAsSynced(batch.map { it.id })
//                    } else {
//                        return@withContext Result.retry()
//                    }
//                } catch (e: Exception) {
//                    return@withContext Result.retry()
//                }
//            }
//
//            Result.success()
//        } catch (e: Exception) {
//            Result.retry()
//        }
//    }
//
//    companion object {
//        private const val WORK_NAME = "database_sync_worker"
//
//        fun schedule(context: Context, intervalHours: Long = 4) {
//            val workRequest = PeriodicWorkRequestBuilder<SyncWorker>(
//                intervalHours,
//                TimeUnit.HOURS
//            )
//                .setInitialDelay(15, TimeUnit.MINUTES)
//                .build()
//
//            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
//                WORK_NAME,
//                ExistingPeriodicWorkPolicy.KEEP,
//                workRequest
//            )
//        }
//
//        fun cancel(context: Context) {
//            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
//        }
//    }
//}