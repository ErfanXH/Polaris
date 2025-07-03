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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * A foreground service dedicated to running the network test and saving the results locally.
 * It does not perform any data syncing.
 */
class TestExecutionService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private lateinit var repository: NetworkRepositoryImpl

    override fun onCreate() {
        super.onCreate()
        Log.d("TestExecutionService", "Service created.")
        val dao = AppDatabaseHelper.getDatabase(this).networkDataDao()
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as android.telephony.TelephonyManager
        repository = NetworkRepositoryImpl(this, tm, dao, NetworkModule.networkDataApi)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TestExecutionService", "Starting foreground service for test execution...")
        startForeground(NOTIF_ID, createNotification())

        serviceScope.launch {
            try {
                Log.d("TestExecutionService", "Running network test in background...")
                repository.runNetworkTest()
                Log.d("TestExecutionService", "Network test finished and saved locally.")
            } catch (e: Exception) {
                Log.e("TestExecutionService", "Error during background test: ${e.message}", e)
            } finally {
                // Stop the service once the work is done
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    /**
     * Creates the notification required for a foreground service.
     */
    private fun createNotification(): Notification {
        val channelId = "network_test_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Network Test Service", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Polaris Test Running")
            .setContentText("Executing network test in background.")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
        Log.d("TestExecutionService", "Service destroyed.")
    }

    companion object {
        private const val NOTIF_ID = 102
    }
}
