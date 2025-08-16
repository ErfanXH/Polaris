package com.netwatcher.polaris.service

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.netwatcher.polaris.data.local.CookieManager
import com.netwatcher.polaris.data.remote.NetworkDataApi
import com.netwatcher.polaris.domain.model.MeasurementRequest
import com.netwatcher.polaris.domain.model.NetworkData
import com.netwatcher.polaris.domain.model.NetworkDataDao
import com.netwatcher.polaris.utils.measurements.measurementConverter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

@HiltWorker
class DataSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val cookieManager: CookieManager,
    private val networkDataApi: NetworkDataApi,
    private val networkDataDao: NetworkDataDao
) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("DataSyncWorker", "Starting data sync work.")


        return try {
            val email = cookieManager.getEmail().firstOrNull()
            if (email.isNullOrBlank()) {
                Log.e("DataSyncWorker", "No User Registered")
                Result.failure()
            }

            val unsynced = networkDataDao.getUnsyncedData(email)
            if (unsynced.isNotEmpty()) {
                Log.d("DataSyncWorker", "Found ${unsynced.size} unsynced items. Syncing...")
                val success = syncDataWithServer(networkDataDao, unsynced)
                if (success) {
                    Log.d("DataSyncWorker", "Sync successful.")
                    Result.success()
                } else {
                    Log.e("DataSyncWorker", "Sync failed. Retrying later.")
                    Result.retry()
                }
            } else {
                Log.d("DataSyncWorker", "No data to sync.")
                Result.success()
            }
        } catch (e: Exception) {
            Log.e("DataSyncWorker", "Error during sync: ${e.message}", e)
            Result.failure()
        }
    }

    private suspend fun syncDataWithServer(
        dao: NetworkDataDao,
        unsynced: List<NetworkData>
    ): Boolean {
        val token = cookieManager.getToken().firstOrNull() ?: return false

        val payload = MeasurementRequest(
            unsynced.map {
                measurementConverter(it)
            }
        )

        val json = Gson().toJson(payload).toRequestBody("application/json".toMediaType())
        val response = networkDataApi.uploadNetworkData(token, json)

        return if (response.isSuccessful) {
            dao.markAsSynced(unsynced.map { it.id })
            true
        } else {
            Log.e("DataSyncWorker", "Server sync failed with code: ${response.code()}")
            false
        }
    }
}
