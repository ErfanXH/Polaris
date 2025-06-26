package com.netwatcher.polaris.worker

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.netwatcher.polaris.AppDatabaseHelper
import com.netwatcher.polaris.data.repository.NetworkRepositoryImpl
import com.netwatcher.polaris.di.NetworkModule
import com.netwatcher.polaris.di.TokenManager
import com.netwatcher.polaris.domain.model.Measurement
import com.netwatcher.polaris.domain.model.MeasurementRequest
import com.netwatcher.polaris.domain.model.NetworkDataDao
import com.netwatcher.polaris.presentation.home.HomeUiState
import com.netwatcher.polaris.presentation.home.HomeViewModel
import com.netwatcher.polaris.utils.TimeStampConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class NetworkMonitorWorker(
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

    @RequiresApi(Build.VERSION_CODES.Q)
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d("NetworkMonitor", "Worker started at ${System.currentTimeMillis()}")

        try {
            // Network Testing
            Log.i("NetworkMonitor", "Starting network tests...")
            val testStart = System.currentTimeMillis()
            val testResult = repository.runNetworkTest()
            Log.i("NetworkMonitor", "Network tests completed in ${System.currentTimeMillis() - testStart}ms. Result: $testResult")

            // Data Sync
            Log.i("NetworkMonitor", "Checking for unsynced data...")
            val syncStart = System.currentTimeMillis()
            val unsyncedCount = database.networkDataDao().getUnsyncedData(NetworkDataDao.getEmail()).size
            Log.i("NetworkMonitor", "Found $unsyncedCount unsynced records")

            if (unsyncedCount > 0) {
                Log.i("NetworkMonitor", "Starting data sync...")
                val syncSuccess = syncUnsyncedData()

                if (syncSuccess) {
                    Log.i("NetworkMonitor", "Data sync completed successfully in ${System.currentTimeMillis() - syncStart}ms")
                    Result.success()
                } else {
                    Log.w("NetworkMonitor", "Data sync failed after ${System.currentTimeMillis() - syncStart}ms")
                    Result.retry()
                }
            } else {
                Log.i("NetworkMonitor", "No unsynced data found")
                Result.success()
            }
        } catch (e: Exception) {
            Log.e("NetworkMonitor", "Worker failed: ${e.javaClass.simpleName} - ${e.message}", e)
            Result.retry()
        }
    }

    private suspend fun syncUnsyncedData(): Boolean {
        val token = TokenManager.getToken().firstOrNull() ?: return false
        val unsyncedData = database.networkDataDao().getUnsyncedData(NetworkDataDao.getEmail())

        Log.d("NetworkMonitor", "unsynced data: $unsyncedData")

        if (unsyncedData.isEmpty()) {
            return true
        }

        return try {
            val measurements = unsyncedData.map { item ->
                Measurement(
                    latitude = item.latitude,
                    longitude = item.longitude,
                    timestamp = TimeStampConverter(item.timestamp),
                    network_type = item.networkType,
                    tac = item.tac,
                    lac = item.lac,
                    rac = item.rac,
                    cell_id = item.cellId,
                    plmn_id = item.plmnId,
                    arfcn = item.arfcn,
                    frequency = item.frequency,
                    frequency_band = item.frequencyBand,
                    rsrp = item.rsrp,
                    rsrq = item.rsrq,
                    rscp = item.rscp,
                    ecIo = item.ecIo,
                    rxLev = item.rxLev,
                    ssRsrp = item.ssRsrp,
                    http_upload = item.httpUploadThroughput,
                    http_download = item.httpDownloadThroughput,
                    ping_time = item.pingTime,
                    dns_response = item.dnsResponse,
                    web_response = item.webResponse,
                    sms_delivery_time = item.smsDeliveryTime
                )
            }

            val payload = MeasurementRequest(measurements)

            val gson = Gson()
            val jsonBody = gson.toJson(payload)

            Log.d("NetworkMonitor", "Serialized JSON: $jsonBody")
            val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

            val response = NetworkModule.networkDataApi.uploadNetworkDataBatch(
                token = token,
                data = requestBody
            )

            Log.d("NetworkMonitor", "response: $response")

            if (response.isSuccessful) {
                database.networkDataDao().markAsSynced(unsyncedData.map { it.id })
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        private const val WORK_NAME = "network_monitor_worker"
        private const val WORK_INTERVAL_MINUTES = 15L

//        fun schedule(context: Context) {
//            Log.d("NetworkMonitorWorker", "Scheduling worker...")
//
//            val workRequest = PeriodicWorkRequestBuilder<NetworkMonitorWorker>(
//                WORK_INTERVAL_MINUTES,
//                TimeUnit.MINUTES
//            )
//                .setInitialDelay(1, TimeUnit.MINUTES) // Start after 1 minute
//                .build()
//
//            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
//                WORK_NAME,
//                ExistingPeriodicWorkPolicy.REPLACE, // Replace existing work
//                workRequest
//            )
//        }

        fun schedule(context: Context) {
            Log.d("NetworkMonitorWorker", "Scheduling worker...")

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<NetworkMonitorWorker>(
                WORK_INTERVAL_MINUTES,
                TimeUnit.MINUTES
            )
                .setInitialDelay(1, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}