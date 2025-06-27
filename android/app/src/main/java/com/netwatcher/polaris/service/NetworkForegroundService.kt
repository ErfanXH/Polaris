package com.netwatcher.polaris.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.netwatcher.polaris.AppDatabaseHelper
import com.netwatcher.polaris.data.repository.NetworkRepositoryImpl
import com.netwatcher.polaris.di.NetworkModule
import com.netwatcher.polaris.di.TokenManager
import com.netwatcher.polaris.domain.model.Measurement
import com.netwatcher.polaris.domain.model.MeasurementRequest
import com.netwatcher.polaris.domain.model.NetworkDataDao
import com.netwatcher.polaris.utils.TimeStampConverter
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.Gson
import com.netwatcher.polaris.R
import kotlinx.coroutines.flow.firstOrNull

class NetworkForegroundService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private lateinit var repository: NetworkRepositoryImpl

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        Log.d("NetworkService", "Service created at ${System.currentTimeMillis()}")
//        startForeground(NOTIF_ID, createNotification())
        val dao = AppDatabaseHelper.getDatabase(this).networkDataDao()
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as android.telephony.TelephonyManager
        repository = NetworkRepositoryImpl(this, tm, dao, NetworkModule.networkDataApi)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("NetworkService", "Starting foreground service...")
        startForeground(NOTIF_ID, createNotification())

        serviceScope.launch {
            try {
                Log.d("NetworkService", "Running network test...")
                repository.runNetworkTest()

                val unsynced = repository.networkDataDao.getUnsyncedData(NetworkDataDao.getEmail())
                if (unsynced.isNotEmpty()) {
                    Log.d("NetworkService", "Syncing ${unsynced.size} items")
                    val success = syncData(unsynced)
                    Log.d("NetworkService", "Sync success: $success")
                }

            } catch (e: Exception) {
                Log.e("NetworkService", "Error: ${e.message}", e)
            } finally {
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private suspend fun syncData(unsynced: List<com.netwatcher.polaris.domain.model.NetworkData>): Boolean {
        val token = TokenManager.getToken().firstOrNull() ?: return false

        val payload = MeasurementRequest(
            unsynced.map {
                Measurement(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    timestamp = TimeStampConverter(it.timestamp),
                    network_type = it.networkType,
                    tac = it.tac,
                    lac = it.lac,
                    rac = it.rac,
                    cell_id = it.cellId,
                    plmn_id = it.plmnId,
                    arfcn = it.arfcn,
                    frequency = it.frequency,
                    frequency_band = it.frequencyBand,
                    rsrp = it.rsrp,
                    rsrq = it.rsrq,
                    rscp = it.rscp,
                    ecIo = it.ecIo,
                    rxLev = it.rxLev,
                    ssRsrp = it.ssRsrp,
                    http_upload = it.httpUploadThroughput,
                    http_download = it.httpDownloadThroughput,
                    ping_time = it.pingTime,
                    dns_response = it.dnsResponse,
                    web_response = it.webResponse,
                    sms_delivery_time = it.smsDeliveryTime
                )
            }
        )

        val json = Gson().toJson(payload).toRequestBody("application/json".toMediaType())
        val response = NetworkModule.networkDataApi.uploadNetworkDataBatch(token, json)

        return if (response.isSuccessful) {
            repository.networkDataDao.markAsSynced(unsynced.map { it.id })
            true
        } else false
    }

    private fun createNotification(): Notification {
        val channelId = "network_monitor_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Network Monitor", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Polaris Network Test Running")
            .setContentText("Monitoring and syncing in background")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val NOTIF_ID = 101
    }
}
